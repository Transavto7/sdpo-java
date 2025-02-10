using System;
using System.Threading;
using System.Threading.Tasks;
using Windows.Devices.Bluetooth;
using Windows.Devices.Radios;
using Windows.Devices.Bluetooth.GenericAttributeProfile;
using Windows.Devices.Enumeration;
using Windows.Storage.Streams;
using System.Globalization;
using System.Linq;

namespace BleAppConsole
{
    internal class Program
    {
        static DeviceInformation deviceInfo = null;
        static DeviceWatcher deviceWatcher = null;
        static BluetoothLEDevice bluetoothLeDevice = null;
        static GattCharacteristicsResult characteristicResult;
        static string[] requestedProperties = { "System.Devices.Aep.DeviceAddress", "System.Devices.Aep.IsConnected" };
        public static string BloodPressureUUID = "00001810-0000-1000-8000-00805f9b34fb";
        static string result;

        static async Task Main(string[] args)
        {
            Thread.CurrentThread.CurrentUICulture = new CultureInfo("en-US");
            if (args.Length == 0)
            {
                Console.WriteLine("error_empty_method");
                return;
            }

            string meth = args[0];
            if (meth.Equals("getTonometerResult"))
            {
                string result = await GetTonometerResultAsync();
                Console.WriteLine(result);
            }
            else
            {
                Console.WriteLine("error_unknown_method");
                return;
            }
        }
        static async Task<string> GetTonometerResultAsync()
        {
            try
            {
                var program = new Program();
                program.StartWatchingDevices();
                while (true)
                {
                    if (deviceInfo == null)
                    {
                        Thread.Sleep(500);
                    }
                    else
                    {
                        await CheckAdapter();

                        bluetoothLeDevice = await BluetoothLEDevice.FromIdAsync(deviceInfo.Id);
                        GattDeviceServicesResult gattDevice = await bluetoothLeDevice.GetGattServicesAsync();

                        if (gattDevice.Status == GattCommunicationStatus.Success)
                        {
                            var services = gattDevice.Services;
                            foreach (var service in services)
                            {
                                if (service.Uuid.ToString() == BloodPressureUUID)
                                //if (service.Uuid == GattCharacteristicUuids.BloodPressureMeasurement)
                                {
                                    characteristicResult = await service.GetCharacteristicsAsync();

                                    if (characteristicResult.Status != GattCommunicationStatus.Success)
                                    {
                                        Console.WriteLine($"Failed to get characteristics. Status: {characteristicResult.Status}. Attempting to reconnect...");
                                        await ReconnectToDevice();
                                    }

                                    if (characteristicResult.Status == GattCommunicationStatus.Success)
                                    {
                                        var characteristics = characteristicResult.Characteristics;
                                        foreach (var characteristic in characteristics)
                                        {
                                            //if (characteristic.Uuid.ToString() == BloodPressureUUID) // doesn't work
                                            if (characteristic.Uuid == GattCharacteristicUuids.BloodPressureMeasurement)
                                            {
                                                bool readed = false;
                                                object lockObj = new object();

                                                characteristic.ValueChanged += (GattCharacteristic sender, GattValueChangedEventArgs args) =>
                                                {
                                                    if (bluetoothLeDevice.ConnectionStatus != BluetoothConnectionStatus.Connected)
                                                    {
                                                        Console.WriteLine("Device is disconnect. Connection in process...");
                                                        Task.Run(async () =>
                                                        {
                                                            try
                                                            {
                                                                await ReconnectToDeviceAsync();
                                                            }
                                                            catch (Exception ex)
                                                            {
                                                                Console.WriteLine($"error_reconnect : {ex.Message}");
                                                            }
                                                        });

                                                    }

                                                    var reader = DataReader.FromBuffer(args.CharacteristicValue);
                                                    byte[] dataArr = new byte[reader.UnconsumedBufferLength];
                                                    reader.ReadBytes(dataArr);

                                                    if (dataArr == null || dataArr.Length == 0)
                                                    {
                                                        Console.WriteLine("DataReader.FromBuffer is null or empty");
                                                        Environment.Exit(102);
                                                    }

                                                    //var hexString = BitConverter.ToString(dataArr);
                                                    //Console.WriteLine($"Received hexString from tonometer : {hexString}");

                                                    //string[] hexValues = hexString.Split('-');
                                                    //int[] intArray = new int[hexValues.Length];

                                                    //for (int i = 0; i < hexValues.Length; i++)
                                                    //{
                                                    //    intArray[i] = Convert.ToInt32(hexValues[i], 16);
                                                    //}
                                                    int[] intArray = dataArr.Select(b => (int)b).ToArray();
                                                    Console.WriteLine("Received data from tonometer: " + string.Join(", ", intArray));

                                                    int systolicPressure = 0;
                                                    int diastolicPressure = 0;
                                                    int pulse = 0;
                                                    if (dataArr.Length < 15)
                                                    {
                                                        Console.WriteLine("Pulse data may be incorrect");
                                                        pulse = dataArr[7];
                                                    }
                                                    else
                                                    {
                                                        pulse = dataArr[14];
                                                    }
                                                    if (pulse <= 0)
                                                    {
                                                        Console.WriteLine("Pulse data is incorrect");
                                                        //Environment.Exit(103);
                                                    }
                                                    systolicPressure = dataArr[1];
                                                    diastolicPressure = dataArr[3];

                                                    result = ("#" + $"{systolicPressure}||{diastolicPressure}||{pulse}");
//                                                    Console.WriteLine("BloodPressureMeasurement result: " + result);

                                                    lock (lockObj)
                                                    {
                                                        readed = true;
                                                        Monitor.PulseAll(lockObj);
                                                    }
                                                };

                                                lock (lockObj)
                                                {
                                                    while (!readed)
                                                    {
                                                        Monitor.Wait(lockObj);
                                                    }
                                                }
                                                deviceWatcher.Stop();
                                                return result;
                                            }
                                        }
                                    }
                                    Console.WriteLine("Characteristic not found");
                                    return "error_characteristic_not_found";
                                    //break;
                                }
                            }
                        }
                        else
                        {
                            Console.WriteLine("error_read_services");
                            //return "error_read_services";
                        }
                        deviceWatcher.Stop();
                        Console.WriteLine("Device Watcher stopped");
                        break;
                    }
                }
            }
            catch (System.Runtime.InteropServices.COMException ex)
            {
                string error = ex.Message + " (HRESULT: " + ex.HResult.ToString() + ")";
                Console.WriteLine("error_windows WinRT " + error);
                Environment.Exit(111);
            }
            catch (Exception ex)
            {
                string error = ex.Message + " (HRESULT: " + ex.HResult.ToString() + ")";
                Console.WriteLine("error_windows " + error);
                Environment.Exit(112);
            }
            
            return null;
        }

        public void StartWatchingDevices()
        {
            deviceWatcher =
                            DeviceInformation.CreateWatcher(
                                    BluetoothLEDevice.GetDeviceSelectorFromPairingState(true),
                                    requestedProperties,
                                    DeviceInformationKind.AssociationEndpoint);

            deviceWatcher.Added += DeviceWatcher_Added;
            deviceWatcher.Updated += DeviceWatcher_Updated;
            deviceWatcher.Removed += DeviceWatcher_Removed;

            deviceWatcher.EnumerationCompleted += DeviceWatcher_EnumerationCompleted;
            deviceWatcher.Stopped += DeviceWatcher_Stopped;

            deviceWatcher.Start();
        }

        private static void StopWatchingDevices()
        {
            if (deviceWatcher != null)
            {
                deviceWatcher.Stop();
                deviceWatcher = null;
            }
        }

        private async void DeviceWatcher_Added(DeviceWatcher sender, DeviceInformation args)
        {
            if (args != null && !string.IsNullOrEmpty(args.Name))
            {
                Console.WriteLine($"Device found: {args.Name}");
                bool connected = await TryConnectToDeviceAsync(args.Id);

                if (!connected)
                {
                    Console.WriteLine($"Unable to connect to {args.Name}. Will try again when detected.");
                    StopWatchingDevices();
                    StartWatchingDevices();
                }
                else
                {
                    deviceInfo = args;
                }
            }
        }

        private async void DeviceWatcher_Stopped(DeviceWatcher sender, object args)
        {
            Console.WriteLine("DeviceWatcher stopped. Restarting...");
            await Task.Delay(TimeSpan.FromSeconds(3));
            StartWatchingDevices();
        }

        private static void DeviceWatcher_EnumerationCompleted(DeviceWatcher sender, object args)
        {
            Console.WriteLine("DeviceWatcher EnumerationCompleted.");
        }

        private static void DeviceWatcher_Removed(DeviceWatcher sender, DeviceInformationUpdate args)
        {
            Console.WriteLine("DeviceWatcher Removed.");
        }

        private static void DeviceWatcher_Updated(DeviceWatcher sender, DeviceInformationUpdate args)
        {
            Console.WriteLine("DeviceWatcher Updated.");
        }

        private async Task<bool> TryConnectToDeviceAsync(string deviceId, int maxRetries = 5)
        {
            int retryCount = 0;
            while (retryCount < maxRetries)
            {
                try
                {
                    Console.WriteLine($"Attempt {retryCount + 1} to connect to device...");
                    var device = await BluetoothLEDevice.FromIdAsync(deviceId);
                    if (device != null)
                    {
                        Console.WriteLine("Successfully connected to device.");
                        return true;
                    }
                }
                catch (Exception ex)
                {
                    Console.WriteLine($"error_connection_failed: {ex.Message}");
                }

                retryCount++;
                await Task.Delay(TimeSpan.FromSeconds(Math.Pow(2, retryCount))); // Delay grows exponentionally
            }

            Console.WriteLine("Failed to connect to device after multiple attempts.");
            return false;
        }

        private static async Task ReconnectToDeviceAsync()
        {
            try
            {
                bluetoothLeDevice = await BluetoothLEDevice.FromIdAsync(bluetoothLeDevice.DeviceId);
                if (bluetoothLeDevice.ConnectionStatus == BluetoothConnectionStatus.Connected)
                {
                    Console.WriteLine("Reconnection succeed.");
                }
                else
                {
                    Console.WriteLine("Reconnection failed.");
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"error_while_reconnecting: {ex.Message}");
            }
        }

        private static async Task ReconnectToDevice()
        {
            // Перепідключення до пристрою
            bluetoothLeDevice.Dispose(); // Звільняємо ресурси
            bluetoothLeDevice = await BluetoothLEDevice.FromIdAsync(deviceInfo.Id);

            if (bluetoothLeDevice == null)
            {
                Console.WriteLine("Reconnection failed: BluetoothLEDevice is null.");
                //return "error_reconnect_failed";
            }

            // Повторне отримання сервісу
            GattDeviceServicesResult gattDeviceRetry = await bluetoothLeDevice.GetGattServicesAsync();
            if (gattDeviceRetry.Status != GattCommunicationStatus.Success)
            {
                Console.WriteLine($"Failed to retrieve GATT services after reconnect. Status: {gattDeviceRetry.Status}");
                //return "error_read_services_after_reconnect";
            }

            // Пошук потрібного сервісу
            var retryServices = gattDeviceRetry.Services;
            var bloodPressureService = retryServices.FirstOrDefault(s => s.Uuid.ToString() == BloodPressureUUID);
            //var bloodPressureService = retryServices.FirstOrDefault(s => s.Uuid == GattCharacteristicUuids.BloodPressureMeasurement);

            if (bloodPressureService == null)
            {
                Console.WriteLine("Blood Pressure Service not found after reconnect.");
                //return "error_service_not_found";
            }

            // Повторна спроба отримати характеристики
            characteristicResult = await bloodPressureService.GetCharacteristicsAsync();
            if (characteristicResult.Status != GattCommunicationStatus.Success)
            {
                Console.WriteLine($"Failed to get characteristics after reconnect. Status: {characteristicResult.Status}");
                //return "error_characteristics_read_failed_after_reconnect";
                Environment.Exit(121);
            }
        }

        private static async Task CheckAdapter()
        {
            var btAdapter = await BluetoothAdapter.GetDefaultAsync();
            if (btAdapter == null)
            {
                Console.WriteLine("bluetooth off");
                //return "error_bluetooth_off";
                Environment.Exit(131);
            }
            if (!btAdapter.IsLowEnergySupported)
            {
                Console.WriteLine("bluetooth low energy unsupported");
                //return "error_bluetooth_low_energy_supported";
                Environment.Exit(132);
            }
            Radio radio = await btAdapter.GetRadioAsync();
            if (radio == null || radio.State != RadioState.On)
            {
                Console.WriteLine("bluetooth radio off");
                //return "error_bluetooth_radio_off";
                Environment.Exit(133);
            }
        }

    }
}
