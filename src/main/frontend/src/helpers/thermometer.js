import {defaultError} from "@/helpers/http-errors";
import axios from "axios";

export async function getTemp() {
    return await axios.post(`device/thermometer`).then(({ data }) => {
        return data;
    }).catch(defaultError);
}

export async function setTestValue(temp) {
    return await axios.post('device/thermometer/test', {
        temp
    }).then(({ data }) => {
        return data;
    }).catch((error) => {
        console.log(error);
        throw error;
    });
}