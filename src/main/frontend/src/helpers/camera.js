import {defaultError} from "@/helpers/http-errors";
import {startWaitTimerRecordMedia} from "@/helpers/media";
import store from "@/store";

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
    }).then(({data}) => {
        return data;
    }).catch(defaultError);
}

export async function stopMedia() {
    return await axios.post(`device/media/stop`).then(async ({data}) => {
        store.state.temp.photo = store.state.inspection.photo;
        store.state.temp.video = store.state.inspection.video;
        await startWaitTimerRecordMedia()
        return data;
    }).catch(defaultError);
}

export async function makeVideoTest() {
    return await axios.post(`device/video/test`).then(({ data }) => {
        return data;
    }).catch(defaultError);
}