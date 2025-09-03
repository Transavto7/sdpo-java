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