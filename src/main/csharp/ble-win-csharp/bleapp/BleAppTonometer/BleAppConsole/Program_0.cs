using System;
using System.Threading;
using System.Threading.Tasks;
using Windows.Devices.Bluetooth;
using Windows.Devices.Radios;
using Windows.Devices.Bluetooth.GenericAttributeProfile;
using Windows.Devices.Enumeration;
using Windows.Storage.Streams;
using System.Globalization;

namespace BleAppConsole
{
    internal class Program
    {
        static DeviceInformation deviceInfo = null;
        static DeviceWatcher deviceWatcher = null;
        static BluetoothLEDevice bluetoothLeDevice = null;
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
                        var btAdapter = await BluetoothAdapter.GetDefaultAsync();
                        if (btAdapter == null)
                        {
                            return "error_bluetooth_off";
                        }
                        if (!btAdapter.IsLowEnergySupported)
                            return "error_bluetooth_low_energy_supported";

                        Radio radio = await btAdapter.GetRadioAsync();
                        if (radio == null || radio.State != RadioState.On)
                        {
                            return "error_bluetooth_radio_off";
                        }

                        bluetoothLeDevice = await BluetoothLEDevice.FromIdAsync(deviceInfo.Id);
                        GattDeviceServicesResult gattDevice = await bluetoothLeDevice.GetGattServicesAsync();

                        if (gattDevice.Status == GattCommunicationStatus.Success)
                        {
                            var services = gattDevice.Services;
                            foreach (var service in services)
                            {
                                if (service.Uuid.ToString() == BloodPressureUUID)
                                {
                                    GattCharacteristicsResult characteristicResult = await service.GetCharacteristicsAsync();

                                    if (characteristicResult.Status == GattCommunicationStatus.Success)
                                    {
                                        var characteristics = characteristicResult.Characteristics;
                                        foreach (var characteristic in characteristics)
                                        {
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
                                                                Console.WriteLine($"Reconnect error: {ex.Message}");
                                                            }
                                                        });

                                                    }

                                                    var reader = DataReader.FromBuffer(args.CharacteristicValue);
                                                    byte[] dataArr = new byte[reader.UnconsumedBufferLength];
                                                    reader.ReadBytes(dataArr);

                                                    var hexString = BitConverter.ToString(dataArr);

                                                    string[] hexValues = hexString.Split('-');
                                                    byte[] byteArray = new byte[hexValues.Length];

                                                    bool pulsePresent = (byteArray[0] & (1 << 5)) != 0;

                                                    if (!pulsePresent)
                                                    {
                                                        Console.WriteLine("Pulse data may be incorrect");
                                                    }


                                                    for (int i = 0; i < hexValues.Length; i++)
                                                    {
                                                        byteArray[i] = Convert.ToByte(hexValues[i], 16);
                                                    }

                                                    int systolicPressure = byteArray[1];  
                                                    int diastolicPressure = byteArray[3]; 
                                                    int pulse = byteArray[14];            

                                                    result = ("#" + $"{systolicPressure}||{diastolicPressure}||{pulse}");

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
                                    break;
                                }
                            }
                        }
                        else
                        {
                            return "error_read_services";
                        }
                        deviceWatcher.Stop();
                        break;
                    }
                }
            }
            catch (System.Runtime.InteropServices.COMException ex)
            {
                string error = ex.Message + " (HRESULT: " + ex.HResult.ToString() + ")";
                return "error_windows WinRT " + error;
            }
            catch (Exception ex)
            {
                string error = ex.Message + " (HRESULT: " + ex.HResult.ToString() + ")";
                return "error_windows " + error;
            }
            //catch
            //{
            //    return "error_windows unknown error";
            //}
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
            await Task.Delay(TimeSpan.FromSeconds(3)); // Затримка перед повторним запуском
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
                    Console.WriteLine($"Connection failed: {ex.Message}");
                }

                retryCount++;
                await Task.Delay(TimeSpan.FromSeconds(Math.Pow(2, retryCount))); // Затримка зростає експоненційно
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
                Console.WriteLine($"Error while reconnecting: {ex.Message}");
            }
        }

    }
}
