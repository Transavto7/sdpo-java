import {defaultError} from "@/helpers/http-errors";

export async function makePhoto() {
    return await axios.post(`device/photo`).then(({ data }) => {
        return data;
    }).catch(defaultError);
}

export async function getSizes() {
    return await axios.get(`device/video/size`).then(({ data }) => {
        return data;
    }).catch(defaultError);
}


export async function makeMedia(driver_id) {
    return await axios.post(`device/media`, {
        driver_id,
        restart: false
    }).then(({ data }) => {
        return data;
    }).catch(defaultError);
}

export async function restartMedia(driver_id) {
    return await axios.post(`device/media`, {
        driver_id,
        restart: true
    }).then(({ data }) => {
        return data;
    }).catch(defaultError);
}

export async function makeVideoTest() {
    return await axios.post(`device/video/test`).then(({ data }) => {
        return data;
    }).catch(defaultError);
}