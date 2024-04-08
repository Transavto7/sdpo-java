import {defaultError} from "@/helpers/http-errors";


export async function getAlcometerResult() {
    return await axios.post(`device/alcometer`).then(({ data }) => {            
        return data;
    }).catch(defaultError);
}

export async function closeAlcometer() {
    return await axios.post(`device/alcometer/close`).then(({ data }) => {            
        return data;
    }).catch(defaultError);
}

export async function enableFastModeAlcometer() {
    return changeMode("fast");
}

export async function enableSlowModeAlcometer() {
       return changeMode("slow");
}

export async function enableModeFromSystemConfig(isFastMode) {
    if (isFastMode) return enableFastModeAlcometer();
    if (!isFastMode) return enableSlowModeAlcometer();
}

async function changeMode(modeName) {
    return await axios.post(`device/alcometer/change-mode`, {mode: modeName}).then(({ data }) => {
        return data;
    }).catch(defaultError);
}