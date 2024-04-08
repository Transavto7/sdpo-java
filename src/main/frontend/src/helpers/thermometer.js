import {defaultError} from "@/helpers/http-errors";

export async function getTemp() {
    return await axios.post(`device/thermometer`).then(({ data }) => {            
        return data;
    }).catch(defaultError);
}