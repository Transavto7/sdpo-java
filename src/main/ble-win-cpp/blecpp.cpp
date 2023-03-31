// blecpp.cpp : This file contains the 'main' function. Program execution begins and ends there.
//
#include "pch.h"
#include "decoders.h"
#include <cstdlib>
#include <string>
#include <fstream>
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
	str << L"Неизвестное устройство ";
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

//int main() {
//	try
//	{
//		BluetoothAdapter btAdapter = BluetoothAdapter::GetDefaultAsync().get();
//		if (btAdapter == NULL) {
//			return 0;
//		}
//		
//		Radio radio = btAdapter.GetRadioAsync().get();
//		if (radio.State() == RadioState::On) {
//			radio.SetStateAsync(RadioState::Off).get();
//			radio.SetStateAsync(RadioState::On).get();
//		}
//		else {
//			radio.SetStateAsync(RadioState::On).get();
//		}
//	}
//	catch (winrt::hresult_error const& ex)
//	{
//		winrt::hresult hr = ex.code();
//		std::string message = winrt::to_string(ex.message());
//		std::wcout << "error: " << hr << std::endl;
//		std::cout << "message: " << message << std::endl;
//	}
//	catch (const std::exception& ex) {
//		std::cout << ex.what() << "\n";
//	}
//	catch (...)
//	{
//		std::wcout << "unknow error" << std::endl;
//	}
//}

//int main() {
//	std::string result = "";
//
//	try
//	{
//		BluetoothAdapter btAdapter = BluetoothAdapter::GetDefaultAsync().get();
//		if (btAdapter == NULL)
//			std::wcout <<  "error_bluetooth_of";
//		if (!btAdapter.IsLowEnergySupported())
//			std::wcout << "error_bluetooth_of";
//		Radio radio = btAdapter.GetRadioAsync().get();
//		if (radio.State() != RadioState::On) {
//			std::wcout << "error_bluetooth_of";
//		}
//
//		uint64_t uuid = stringToMac("34:14:b5:8f:83:4e");
//		BluetoothLEDevice dev = BluetoothLEDevice::FromBluetoothAddressAsync(uuid).get();
//	
//		if (dev == NULL) {
//			std::wcout << "error" << std::endl;
//			return 0;
//		}
//
//		GattDeviceServicesResult rs = dev.GetGattServicesAsync().get();
//		std::mutex m;
//		std::condition_variable cv;
//
//		if (rs.Status() == GattCommunicationStatus::Success) {
//			auto services = rs.Services();
//			for (GattDeviceService service : services) {
//				auto uuid = service.Uuid();
//
//				if (uuid == GattServiceUuids::BloodPressure()) {
//
//					// read blood pressure
//					auto characteristics = service.GetAllCharacteristics();
//					for (GattCharacteristic characteristic : characteristics) {
//						GattReadResult res = characteristic.ReadValueAsync().get();
//						if (characteristic.Uuid() == GattCharacteristicUuids::BloodPressureMeasurement()) {
//							while (true) {
//								std::wcout << UUIDToString(characteristic.Uuid()) << std::endl;
//								GattCommunicationStatus status = characteristic.WriteClientCharacteristicConfigurationDescriptorAsync(GattClientCharacteristicConfigurationDescriptorValue::Indicate).get();
//
//								switch (status) {
//								case GattCommunicationStatus::AccessDenied:
//									printf("access denied\n");
//									break;
//								case GattCommunicationStatus::ProtocolError:
//									printf("protocol error\n");
//									break;
//								case GattCommunicationStatus::Unreachable:
//									printf("unreachable\n");
//									break;
//								}
//
//
//								if (status == GattCommunicationStatus::Success) {
//									std::condition_variable cv;
//									bool readed = false;
//									std::wcout << "reading" << std::endl;
//									characteristic.ValueChanged([&result, &readed, &cv](GattCharacteristic const& charateristic, GattValueChangedEventArgs const& args) {
//										auto data = args.CharacteristicValue().data();
//										result = std::to_string(data[1]) + "||" + std::to_string(data[3]) + "||" + std::to_string(data[7]);
//										std::wcout << result.data() << std::endl;
//										readed = true;
//										cv.notify_all();
//									});
//
//									std::unique_lock l(m);
//									cv.wait(l, [&readed] {
//										return readed;
//									});
//								}
//								else {
//									std::wcout << "error set indicate" << std::endl;
//								}
//							}
//						}
//
//					}
//				}
//			}
//		}
//		else {
//			std::wcout << "cant find services " << GattCommunicationStatusToString(rs.Status()) << std::endl;
//			std::wcout << "error_read_services";
//		}
//
//	}
//	catch (winrt::hresult_error const& ex)
//	{
//		winrt::hresult hr = ex.code();
//		std::string message = winrt::to_string(ex.message());
//		std::wcout << "error: " << hr << std::endl;
//		std::cout << "message: " << message << std::endl;
//		std::wcout << "error_windows";
//	}
//	catch (const std::exception& ex) {
//		std::cout << ex.what() << "\n";
//		std::wcout << "error_windows";
//	}
//	catch (...)
//	{
//		std::wcout << "unknow error" << std::endl;
//		std::wcout << "error_windows";
//	}
//
//	std::wcout << result.data();
//}

//int main() {
//	std::string result = "";
//
//	try
//	{
//		winrt::init_apartment();
//
//		uint64_t addr = GetFirstAdvertisingBLEAddr();
//		if (addr == 0) {
//			return NULL;
//		}
//
//		std::wstring wsAddr = AddrToString(addr);
//		std::string strAddr(wsAddr.begin(), wsAddr.end());
//		std::wstring wsName = GetDeviceName(addr);
//		std::string strName(wsName.begin(), wsName.end());
//		result = strAddr + " || " + strName;
//	}
//	catch (winrt::hresult_error const& ex)
//	{
//		winrt::hresult hr = ex.code();
//		std::string message = winrt::to_string(ex.message());
//		std::wcout << "error: " << hr << std::endl;
//		std::cout << "message: " << message << std::endl;
//	}
//	catch (const std::exception& ex) {
//		std::cout << ex.what() << "\n";
//	}
//	catch (...)
//	{
//		std::wcout << "unknow error" << std::endl;
//	}
//
//	std::cout << result << std::endl;
//	return 0;
//}

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
				std::cout << "uuid" << std::endl;

				if (uuid == GattServiceUuids::BloodPressure()) {
					std::cout << "blood" << std::endl;

					// read blood pressure
					auto characteristics = service.GetAllCharacteristics();
					for (GattCharacteristic characteristic : characteristics) {
						GattReadResult res = characteristic.ReadValueAsync().get();
						std::cout << "blood" << std::endl;
						if (characteristic.Uuid() == GattCharacteristicUuids::BloodPressureMeasurement()) {
							std::condition_variable cv;
							bool readed = false;
							std::cout << "listen" << std::endl;

							characteristic.ValueChanged([&result, &readed, &cv, &m](GattCharacteristic const& charateristic, GattValueChangedEventArgs const& args) {
								auto data = args.CharacteristicValue().data();
								result = std::to_string(data[1]) + "||" + std::to_string(data[3]) + "||" + std::to_string(data[7]);
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
								return (*env).NewStringUTF("success");
							} else {
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
