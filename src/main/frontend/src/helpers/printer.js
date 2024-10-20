import axios from 'axios';
import {defaultError} from "@/helpers/http-errors";

export async function print() {
    return await axios.post(`device/printer`).then(({ data }) => {
        return data;
    }).catch(defaultError);
}

export async function printQr(driverId, type) {
    return await axios.post(`/device/printer/qr`, {
        'driver' : driverId,
        'type' : type
    }).then(({ data }) => {
        return data;
    }).catch(defaultError);
}