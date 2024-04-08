import {defaultError} from "@/helpers/http-errors";

export async function getPressure() {
    return await axios.post(`device/tonometer`).then(({ data }) => {            
        return data;
    }).catch(defaultError);
}

export async function disableTonometer() {
    return await axios.post(`device/tonometer/disable`).then(({ data }) => {            
        return data;
    }).catch(defaultError);
}

export async function setConnection(status = 'connect') {
    return await axios.post('device/tonometer/connect', {
        status, 
    }).then(({ data }) => {            
        return data;
    }).catch(defaultError);
}

export async function checkDevices() {
    return await axios.post(`device/scan`).then(({ data }) => {
        return data;
    }).catch((error) => {
        console.log(error);
    });
}