import {defaultError} from "@/helpers/http-errors";
import axios from "axios";


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

export async function closeAlcometrSocket() {
    return await axios.post(`/device/alcometer/status/stop`).then(({data}) => {
        return data;
    });
}

export async function enableSlowModeAlcometer() {
       return changeMode("slow");
}

async function changeMode(modeName) {
    return await axios.post(`device/alcometer/change-mode`, {mode: modeName}).then(({ data }) => {
        return data;
    }).catch(defaultError);
}