import axios from 'axios';
import {defaultError} from "@/helpers/http-errors";

export async function print() {
    return await axios.post(`device/printer`).then(({ data }) => {
        return data;
    }).catch(defaultError);
}
