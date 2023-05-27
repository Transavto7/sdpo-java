#include "pch.h"
#include "decoders.h"
#include <cstdlib>
#include <string>
#include <fstream>
#include <vector>
#include <iostream>
#include "ru_nozdratenko_sdpo_lib_Bluetooth.h"

#include <winrt/Windows.Foundation.h>
#include <winrt/Windows.Devices.Radios.h>
#include <winrt/Windows.Devices.Bluetooth.h>
#include <winrt/Windows.Devices.Bluetooth.Advertisement.h>
#include <winrt/Windows.Devices.Bluetooth.GenericAttributeProfile.h>

using namespace winrt::Windows::Foundation;
using namespace winrt::Windows::Devices::Radios;
using namespace winrt::Windows::Devices::Bluetooth;
using namespace winrt::Windows::Devices::Bluetooth::Advertisement;
using namespace winrt::Windows::Devices::Bluetooth::GenericAttributeProfile;

std::wstring GetDeviceName(uint64_t deviceAddr) {
	BluetoothLEDevice dev = BluetoothLEDevice::FromBluetoothAddressAsync(deviceAddr).get();

	auto gapServicesResult = dev.GetGattServicesForUuidAsync(GattServiceUuids::GenericAccess(), BluetoothCacheMode::Uncached).get();
	if (gapServicesResult.Status() == GattCommunicationStatus::Success) {
		auto gapServices = gapServicesResult.Services();
		if (gapServices.Size() > 0) {
			GattDeviceService genericAccessSvc = gapServices.GetAt(0);
			if (genericAccessSvc) {
				auto gapDeviceNameChrs = genericAccessSvc.GetCharacteristics(GattCharacteristicUuids::GapDeviceName());
				if (gapDeviceNameChrs.Size() == 1) {
					GattCharacteristic gapDeviceNameChr = gapDeviceNameChrs.GetAt(0);

					GattReadResult readRes = gapDeviceNameChr.ReadValueAsync().get();
					if (readRes.Status() == GattCommunicationStatus::Success) {
						DataReader reader = DataReader::FromBuffer(readRes.Value());
						return reader.ReadString(reader.UnconsumedBufferLength()).c_str();
					}
				}
			}
		}
	}

	std::wstring devName = dev.Name().c_str();
	if (!devName.empty()) {
		return devName;
	}

	std::wstringstream str;
	str << L"Ќеизвестное устройство ";
	return str.str();
}

uint64_t GetFirstAdvertisingBLEAddr()
{
	std::mutex m;
	std::condition_variable cv;

	BluetoothLEAdvertisementWatcher advWatcher;
	uint64_t addr = 0;
	bool stopCalled = false;
	auto recvToken = advWatcher.Received([&addr, &m, &cv](BluetoothLEAdvertisementWatcher watcher, BluetoothLEAdvertisementReceivedEventArgs eventArgs) {
		addr = eventArgs.BluetoothAddress();
		watcher.Stop();
		std::unique_lock l(m);
		cv.notify_all();
	});

	auto stoppedToken = advWatcher.Stopped([&stopCalled, &m, &cv](BluetoothLEAdvertisementWatcher watcher, BluetoothLEAdvertisementWatcherStoppedEventArgs eventArgs) {
		std::unique_lock l(m);
		stopCalled = true;
		cv.notify_all();
	});

	{
		std::unique_lock l(m);
		advWatcher.Start();
		cv.wait(l, [&addr, &stopCalled, &advWatcher] { return addr != 0 && stopCalled && (advWatcher.Status() == BluetoothLEAdvertisementWatcherStatus::Aborted || advWatcher.Status() == BluetoothLEAdvertisementWatcherStatus::Stopped); });
	}

	// remove event handlers
	advWatcher.Received(recvToken);
	advWatcher.Stopped(stoppedToken);

	return addr;
}

std::string jstring2string(JNIEnv* env, jstring jStr) {
	if (!jStr)
		return "";

	const jclass stringClass = env->GetObjectClass(jStr);
	const jmethodID getBytes = env->GetMethodID(stringClass, "getBytes", "(Ljava/lang/String;)[B");
	const jbyteArray stringJbytes = (jbyteArray)env->CallObjectMethod(jStr, getBytes, env->NewStringUTF("UTF-8"));

	size_t length = (size_t)env->GetArrayLength(stringJbytes);
	jbyte* pBytes = env->GetByteArrayElements(stringJbytes, NULL);

	std::string ret = std::string((char*)pBytes, length);
	env->ReleaseByteArrayElements(stringJbytes, pBytes, JNI_ABORT);

	env->DeleteLocalRef(stringJbytes);
	env->DeleteLocalRef(stringClass);
	return ret;
}


JNIEXPORT jstring JNICALL Java_ru_nozdratenko_sdpo_lib_Bluetooth_findDevice(JNIEnv* env, jclass thisObject)
{
	std::string result = "";
	
	try
	{
		BluetoothAdapter btAdapter = BluetoothAdapter::GetDefaultAsync().get();
		if (btAdapter == NULL)
			return (*env).NewStringUTF("error_bluetooth_adapter_not_found");
		if (!btAdapter.IsLowEnergySupported())
			return (*env).NewStringUTF("error_bluetooth_low_energy");
		Radio radio = btAdapter.GetRadioAsync().get();
		if (radio.State() != RadioState::On) {
			return (*env).NewStringUTF("error_bluetooth_radio_off");
		}

		winrt::init_apartment();

		uint64_t addr = GetFirstAdvertisingBLEAddr();
		if (addr == 0) {
			return (*env).NewStringUTF("error_address_not_found");
		}

		std::wstring wsAddr = AddrToString(addr);
		std::string strAddr(wsAddr.begin(), wsAddr.end());
		result = strAddr;
	}
	catch (winrt::hresult_error const& ex)
	{
		winrt::hresult hr = ex.code();
		std::string message = winrt::to_string(ex.message());
		std::wcout << "error: " << hr << std::endl;
		std::cout << "message: " << message << std::endl;
		return (*env).NewStringUTF(message.data());
	}
	catch (const std::exception& ex) {
		std::cout << ex.what() << "\n";
	}
	catch (...)
	{
		std::wcout << "unknow error" << std::endl;
	}

	return (*env).NewStringUTF(result.data());
}

JNIEXPORT void JNICALL Java_ru_nozdratenko_sdpo_lib_Bluetooth_restart(JNIEnv* env, jclass thisObject) {
	try
	{
		BluetoothAdapter btAdapter = BluetoothAdapter::GetDefaultAsync().get();
		if (btAdapter == NULL) {
			return;
		}

		Radio radio = btAdapter.GetRadioAsync().get();
		if (radio.State() == RadioState::On) {
			radio.SetStateAsync(RadioState::Off).get();
			radio.SetStateAsync(RadioState::On).get();
		}
		else {
			radio.SetStateAsync(RadioState::On).get();
		}
	}
	catch (winrt::hresult_error const& ex) {
	} catch (const std::exception& ex) {
	} catch (...) { }
}

JNIEXPORT jstring JNICALL Java_ru_nozdratenko_sdpo_lib_Bluetooth_getTonometerResult(JNIEnv* env, jclass thisObject, jstring strUuid)
{
	std::string result = "";

	try
	{
		BluetoothAdapter btAdapter = BluetoothAdapter::GetDefaultAsync().get();
		if (btAdapter == NULL)
			return (*env).NewStringUTF("error_bluetooth_of");
		if (!btAdapter.IsLowEnergySupported())
			return (*env).NewStringUTF("error_bluetooth_of");
		Radio radio = btAdapter.GetRadioAsync().get();
		if (radio.State() != RadioState::On) {
			return (*env).NewStringUTF("error_bluetooth_of");
		}

		uint64_t uuid = stringToMac(jstring2string(env, strUuid));
		BluetoothLEDevice dev = BluetoothLEDevice::FromBluetoothAddressAsync(uuid).get();

		if (dev == NULL) {
			return (*env).NewStringUTF("error_device_not_found");
		}

		GattDeviceServicesResult rs = dev.GetGattServicesAsync().get();
		std::mutex m;
		std::condition_variable cv;

		if (rs.Status() == GattCommunicationStatus::Success) {
			auto services = rs.Services();

			for (GattDeviceService service : services) {
				auto uuid = service.Uuid();

				if (uuid == GattServiceUuids::BloodPressure()) {
					std::cout << "blood" << std::endl;

					// read blood pressure
					auto characteristics = service.GetAllCharacteristics();
					for (GattCharacteristic characteristic : characteristics) {
						std::cout << "blood" << std::endl;
						if (characteristic.Uuid() == GattCharacteristicUuids::BloodPressureMeasurement()) {
							std::condition_variable cv;
							bool readed = false;
							std::cout << "listen" << std::endl;

							characteristic.ValueChanged([&result, &readed, &cv, &m](GattCharacteristic const& charateristic, GattValueChangedEventArgs const& args) {
								auto data = args.CharacteristicValue().data();
								result = std::to_string(data[1]) + "||" + std::to_string(data[3]) + "||" + std::to_string(data[14]);
								std::cout << result << std::endl;
								readed = true;
								cv.notify_all();
							});

							{
								std::unique_lock l(m);
								cv.wait(l, [&readed] {
									return readed;
								});
							}
						}

					}
				}
			}
		}
		else {
			return (*env).NewStringUTF("error_read_services");
		}

	}
	catch (winrt::hresult_error const& ex)
	{
		winrt::hresult hr = ex.code();
		std::string message = winrt::to_string(ex.message());
		std::wcout << "error: " << hr << std::endl;
		std::cout << "message: " << message << std::endl;
		return (*env).NewStringUTF("error_windows");
	}
	catch (const std::exception& ex) {
		std::cout << ex.what() << "\n";
		return (*env).NewStringUTF("error_windows");
	}
	catch (...)
	{
		std::wcout << "unknow error" << std::endl;
		return (*env).NewStringUTF("error_windows");
	}

	return (*env).NewStringUTF(result.data());
}

JNIEXPORT jstring JNICALL Java_ru_nozdratenko_sdpo_lib_Bluetooth_setIndicate(JNIEnv* env, jclass thisObject, jstring strUuid)
{
	std::string result = "";

	try
	{
		BluetoothAdapter btAdapter = BluetoothAdapter::GetDefaultAsync().get();
		if (btAdapter == NULL)
			return (*env).NewStringUTF("error_bluetooth_of");
		if (!btAdapter.IsLowEnergySupported())
			return (*env).NewStringUTF("error_bluetooth_of");
		Radio radio = btAdapter.GetRadioAsync().get();
		if (radio.State() != RadioState::On) {
			return (*env).NewStringUTF("error_bluetooth_of");
		}

		uint64_t uuid = stringToMac(jstring2string(env, strUuid));
		BluetoothLEDevice dev = BluetoothLEDevice::FromBluetoothAddressAsync(uuid).get();

		if (dev == NULL) {
			return (*env).NewStringUTF("error_device_not_found");
		}

		GattDeviceServicesResult rs = dev.GetGattServicesAsync().get();
		std::mutex m;
		std::condition_variable cv;

		if (rs.Status() == GattCommunicationStatus::Success) {
			auto services = rs.Services();

			for (GattDeviceService service : services) {
				auto uuid = service.Uuid();

				if (uuid == GattServiceUuids::BloodPressure()) {
					// read blood pressure
					auto characteristics = service.GetAllCharacteristics();
					for (GattCharacteristic characteristic : characteristics) {
						if (characteristic.Uuid() == GattCharacteristicUuids::BloodPressureMeasurement()) {
							GattCommunicationStatus status = characteristic.WriteClientCharacteristicConfigurationDescriptorAsync(GattClientCharacteristicConfigurationDescriptorValue::Indicate).get();
							if (status == GattCommunicationStatus::Success) {
								std::cout << "success" << std::endl;
								return (*env).NewStringUTF("success");
							} else {
								std::cout << "error indicate" << std::endl;
								return (*env).NewStringUTF("error_characteristic_write");
							}
						}

					}
				}
			}
		}
		else {
			return (*env).NewStringUTF("error_read_services");
		}

	} catch (winrt::hresult_error const& ex)
	{
		winrt::hresult hr = ex.code();
		std::string message = winrt::to_string(ex.message());
		std::wcout << "error: " << hr << std::endl;
		std::cout << "message: " << message << std::endl;
		return (*env).NewStringUTF("error_windows");
	}
	catch (const std::exception& ex) {
		std::cout << ex.what() << "\n";
		return (*env).NewStringUTF("error_windows");
	}
	catch (...)
	{
		std::wcout << "unknow error" << std::endl;
		return (*env).NewStringUTF("error_windows");
	}

	return (*env).NewStringUTF(result.data());
}

JNIEXPORT jstring JNICALL Java_ru_nozdratenko_sdpo_lib_Bluetooth_setConnection(JNIEnv* env, jclass thisObject, jstring strUuid) {
	std::string result = "wait";

	try
	{
		BluetoothAdapter btAdapter = BluetoothAdapter::GetDefaultAsync().get();
		if (btAdapter == NULL)
			return (*env).NewStringUTF("error_bluetooth_of");
		if (!btAdapter.IsLowEnergySupported())
			return (*env).NewStringUTF("error_bluetooth_of");
		Radio radio = btAdapter.GetRadioAsync().get();
		if (radio.State() != RadioState::On) {
			return (*env).NewStringUTF("error_bluetooth_of");
		}

		uint64_t uuid = stringToMac(jstring2string(env, strUuid));
		BluetoothLEDevice dev = BluetoothLEDevice::FromBluetoothAddressAsync(uuid).get();

		if (dev == NULL) {
			return (*env).NewStringUTF("error_device_not_found");
		}

		GattDeviceServicesResult rs = dev.GetGattServicesAsync().get();
		std::mutex m;
		std::condition_variable cv;

		if (rs.Status() == GattCommunicationStatus::Success) {
			auto services = rs.Services();

			for (GattDeviceService service : services) {
				auto uuid = service.Uuid();
				if (UUIDToString(uuid) == L"233BF000-5A34-1B6D-975C-000D5690ABE4") {
					auto characteristics = service.GetAllCharacteristics();

					for (GattCharacteristic characteristic : characteristics) {
						DataWriter writer;
						writer.WriteByte(0x03);
						writer.WriteByte(0x01);
						writer.WriteByte(0xA6);
						writer.WriteByte(0x00);
						IBuffer buffer = writer.DetachBuffer();
						GattCommunicationStatus stat = characteristic.WriteValueAsync(buffer, GattWriteOption::WriteWithoutResponse).get();
						if (stat == GattCommunicationStatus::Success) {
							if (UUIDToString(characteristic.Uuid()) != L"233BF001-5A34-1B6D-975C-000D5690ABE4") {
								continue;
							}

							std::cout << "SET" << std::endl;

							// получение текущей даты и времени
							auto now = std::chrono::system_clock::now();
							auto time = std::chrono::system_clock::to_time_t(now);
							std::tm tm;
							localtime_s(&tm, &time);

							// запись даты и времени в формате байтов
							std::vector<uint8_t> bytes_date = {
								0x08,
								0x01,
								0x01,
								static_cast<uint8_t>(tm.tm_year % 100), // год в формате yy (от 0 до 99)
								static_cast<uint8_t>(tm.tm_mon + 1),    // мес€ц от 1 до 12
								static_cast<uint8_t>(tm.tm_mday),       // день мес€ца от 1 до 31
								static_cast<uint8_t>(tm.tm_hour),       // час от 0 до 23
								static_cast<uint8_t>(tm.tm_min),        // минута от 0 до 59
								static_cast<uint8_t>(tm.tm_sec)         // секунда от 0 до 59
							};

							DataWriter writer;
							writer.WriteBytes(bytes_date);
							IBuffer buffer = writer.DetachBuffer();
							GattCommunicationStatus stat = characteristic.WriteValueAsync(buffer).get();
							std::wcout << buffer.data() << std::endl;

							result = "set";
						}
					}
				}
			}

			for (GattDeviceService service : services) {
				auto uuid = service.Uuid();

				if (uuid == GattServiceUuids::Battery()) {
					for (GattCharacteristic characteristic : service.GetAllCharacteristics()) {
						if (characteristic.Uuid() == GattCharacteristicUuids::BatteryLevel()) {
							std::condition_variable cv;
							bool readed = false;

							std::cout << "listen" << std::endl;
							auto result = characteristic.ReadValueAsync().get().Value().data();
							printf("Battery Level: %d%%\n", result[0]);
						}

					}
				}
			}
		}
		else {
			return (*env).NewStringUTF("error_read_services");
		}
	}
	catch (winrt::hresult_error const& ex)
	{
		winrt::hresult hr = ex.code();
		std::string message = winrt::to_string(ex.message());
		std::wcout << "error: " << hr << std::endl;
		std::cout << "message: " << message << std::endl;
		return (*env).NewStringUTF("error_windows");
	}
	catch (const std::exception& ex) {
		std::cout << ex.what() << "\n";
		return (*env).NewStringUTF("error_windows");
	}
	catch (...)
	{
		std::wcout << "unknow error" << std::endl;
		return (*env).NewStringUTF("error_windows");
	}

	return (*env).NewStringUTF(result.data());
}

std::vector<std::pair<std::string, std::string>> scanBluetoothDevices() {
	std::vector<std::pair<std::string, std::string>> devices;

	// Get Bluetooth LE devices
	auto deviceInfoCollection = DeviceInformation::FindAllAsync(BluetoothLEDevice::GetDeviceSelector()).get();

	// Iterate over devices and get MAC addresses
	for (const auto& deviceInfo : deviceInfoCollection) {
		auto bleDevice = BluetoothLEDevice::FromIdAsync(deviceInfo.Id()).get();
		if (bleDevice) {
			std::stringstream ss;
			ss << std::hex << bleDevice.BluetoothAddress(); // преобразование uint_64 в шестнадцатеричное число
			std::string addressStr = ss.str(); // получение строки из stringstream

			std::string formattedAddressStr;
			for (int i = 0; i < addressStr.size(); i += 2) {
				formattedAddressStr += addressStr.substr(i, 2);
				if (i < addressStr.size() - 2) {
					formattedAddressStr += ':';
				}
			}

			auto deviceName = winrt::to_string(bleDevice.Name()); // получение имени устройства
			devices.push_back({ formattedAddressStr, deviceName }); // добавление в вектор пары "MAC-адрес, им€ устройства"
		}
	}

	// Convert vector of strings to array of Java strings
	/*jobjectArray jMacAddresses = env->NewObjectArray(macAddresses.size(), env->FindClass("java/lang/String"), nullptr);
	for (int i = 0; i < macAddresses.size(); i++) {
		jstring jMacAddress = env->NewStringUTF(macAddresses[i].c_str());
		env->SetObjectArrayElement(jMacAddresses, i, jMacAddress);
	}*/

	return devices;
}

jobject createHashMap(JNIEnv* env, const std::vector<std::pair<std::string, std::string>>& vec) {
	// ѕолучаем класс HashMap
	jclass hashMapClass = env->FindClass("java/util/HashMap");
	// ѕолучаем конструктор без параметров
	jmethodID hashMapConstructor = env->GetMethodID(hashMapClass, "<init>", "()V");
	// —оздаем новый объект HashMap
	jobject hashMap = env->NewObject(hashMapClass, hashMapConstructor);
	// ѕолучаем метод put дл€ добавлени€ элементов в HashMap
	jmethodID hashMapPutMethod = env->GetMethodID(hashMapClass, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

	// ƒобавл€ем элементы в HashMap
	for (const auto& pair : vec) {
		jstring jKey = env->NewStringUTF(pair.first.c_str());
		jstring jValue = env->NewStringUTF(pair.second.c_str());
		env->CallObjectMethod(hashMap, hashMapPutMethod, jKey, jValue);
		env->DeleteLocalRef(jKey);
		env->DeleteLocalRef(jValue);
	}

	return hashMap;
}

JNIEXPORT jobject JNICALL Java_ru_nozdratenko_sdpo_lib_Bluetooth_scanBluetoothDevices(JNIEnv* env, jclass thisObject) {
	std::vector<std::pair<std::string, std::string>> macs = scanBluetoothDevices();
	return createHashMap(env, macs);
}