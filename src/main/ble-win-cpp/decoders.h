#pragma once

uint64_t stringToMac(std::string const& s);
winrt::guid StringToGuid(const std::string& str);
std::wstring UUIDToString(const winrt::guid & uuid);
std::wstring ServiceToString(const winrt::guid & uuid);
std::wstring CharacteristicToString(const winrt::guid & uuid);
std::wstring AdvertisementTypeToString(BluetoothLEAdvertisementType type);
std::wstring AdvertisementDataTypeToString(uint8_t dataType);
std::wstring AddrToString(uint64_t addr);
std::wstring GattCommunicationStatusToString(GattCommunicationStatus status);