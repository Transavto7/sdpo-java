#include <Windows.h>
#include <setupapi.h>
#include <devguid.h>
#include <regstr.h>
#include <iostream>
#include <string>
#include <vector>
#include <codecvt>


#include "ru_nozdratenko_sdpo_lib_COMPorts.h"

#pragma comment(lib, "setupapi.lib")



bool containsSubstring(std::wstring wstr, std::string str) {
    std::wstring_convert<std::codecvt_utf8_utf16<wchar_t>> converter;
    std::string converted_str = converter.to_bytes(wstr);
    return (converted_str.find(str) != std::string::npos);
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

jstring wstringToJstring(JNIEnv* env, const std::wstring& wstr) {
    jclass stringClass = env->FindClass("java/lang/String");
    jmethodID stringConstructor = env->GetMethodID(stringClass, "<init>", "([BLjava/lang/String;)V");

    std::wstring_convert<std::codecvt_utf8_utf16<wchar_t>> converter;
    std::string utf8str = converter.to_bytes(wstr);
    jbyteArray byteArray = env->NewByteArray(utf8str.length());
    env->SetByteArrayRegion(byteArray, 0, utf8str.length(), reinterpret_cast<const jbyte*>(utf8str.c_str()));

    jstring charsetName = env->NewStringUTF("UTF-8");
    return reinterpret_cast<jstring>(env->NewObject(stringClass, stringConstructor, byteArray, charsetName));
}

JNIEXPORT jstring JNICALL Java_ru_nozdratenko_sdpo_lib_COMPorts_getComPort(JNIEnv* env, jclass thisObject, jstring strVid) {
    HDEVINFO deviceInfoSet = SetupDiGetClassDevs(&GUID_DEVCLASS_PORTS, NULL, NULL, DIGCF_PRESENT);

    if (deviceInfoSet == INVALID_HANDLE_VALUE)
    {
        std::cerr << "Failed to get device information set." << std::endl;
        return (*env).NewStringUTF("error_get_device");
    }

    DWORD index = 0;
    SP_DEVINFO_DATA deviceInfoData = { sizeof(SP_DEVINFO_DATA) };

    while (SetupDiEnumDeviceInfo(deviceInfoSet, index++, &deviceInfoData))
    {
        HKEY hkey = SetupDiOpenDevRegKey(deviceInfoSet, &deviceInfoData, DICS_FLAG_GLOBAL, 0, DIREG_DEV, KEY_READ);

        if (hkey == INVALID_HANDLE_VALUE)
        {
            continue;
        }

        TCHAR portName[256];
        DWORD size = sizeof(portName);

        if (RegQueryValueEx(hkey, REGSTR_VAL_PORTNAME, NULL, NULL, (LPBYTE)portName, &size) != ERROR_SUCCESS)
        {
            RegCloseKey(hkey);
            continue;
        }

        std::wstring portNameString = portName;

        if (portNameString.find(L"COM") == std::wstring::npos)
        {
            RegCloseKey(hkey);
            continue;
        }

        TCHAR name[256];
        size = sizeof(name);
        if (RegQueryValueEx(hkey, L"SymbolicName", NULL, NULL, (LPBYTE)name, &size) != ERROR_SUCCESS)
        {
            RegCloseKey(hkey);
            continue;
        }

        std::wstring vidString = name;
        std::string vid = jstring2string(env, strVid);

        if (containsSubstring(vidString, vid)) {
            RegCloseKey(hkey);
            return wstringToJstring(env, portNameString);
        }

        RegCloseKey(hkey);
    }

    SetupDiDestroyDeviceInfoList(deviceInfoSet);

    return (*env).NewStringUTF("error_unknow");
}

int main()
{
    HDEVINFO deviceInfoSet = SetupDiGetClassDevs(&GUID_DEVCLASS_PORTS, NULL, NULL, DIGCF_PRESENT);

    if (deviceInfoSet == INVALID_HANDLE_VALUE)
    {
        std::cerr << "Failed to get device information set." << std::endl;
        return -1;
    }

    DWORD index = 0;
    SP_DEVINFO_DATA deviceInfoData = { sizeof(SP_DEVINFO_DATA) };

    while (SetupDiEnumDeviceInfo(deviceInfoSet, index++, &deviceInfoData))
    {
        HKEY hkey = SetupDiOpenDevRegKey(deviceInfoSet, &deviceInfoData, DICS_FLAG_GLOBAL, 0, DIREG_DEV, KEY_READ);

        if (hkey == INVALID_HANDLE_VALUE)
        {
            continue;
        }

        TCHAR portName[256];
        DWORD size = sizeof(portName);

        if (RegQueryValueEx(hkey, REGSTR_VAL_PORTNAME, NULL, NULL, (LPBYTE)portName, &size) != ERROR_SUCCESS)
        {
            RegCloseKey(hkey);
            continue;
        }

        std::wstring portNameString = portName;

        if (portNameString.find(L"COM") == std::wstring::npos)
        {
            RegCloseKey(hkey);
            continue;
        }

        TCHAR vid[256];
        size = sizeof(vid);
        if (RegQueryValueEx(hkey, L"SymbolicName", NULL, NULL, (LPBYTE)vid, &size) != ERROR_SUCCESS)
        {
            RegCloseKey(hkey);
            continue;
        }

        std::wstring vidString = vid;

        if (vidString.find(L"VID_")) {
            std::wcout << "Port name: " << portNameString << std::endl;
        }

        RegCloseKey(hkey);
    }

    SetupDiDestroyDeviceInfoList(deviceInfoSet);

    return 0;
}