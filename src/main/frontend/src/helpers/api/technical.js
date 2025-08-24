import store from "@/store";
import axios from "axios";
import {defaultError} from "@/helpers/http-errors";
import {useToast} from "vue-toastification";

const toast = useToast();

export async function getCarByNumberOrHash(needle) {
    return await axios.post(`api/car/by-number-or-hash`, {
        needle,
    })
        .then(({data}) => {
            console.log(data);
            return data;
        }).catch(defaultError);
}

export async function getLastMedicalInspection(driverHashId) {
    return await axios.post(`api/medical/by-driver-hash`, {
        driverHashId
    })
        .then(({data}) => {
            return data;
        }).catch(defaultError);
}

export async function saveTechnicalInspection(inspection = store.state.technical) {
    return await axios.post(`technical/inspection/save`, inspection)
        .then(({data}) => {
            return data;
        }).catch(defaultError);
}

export async function replayTechnicalPrint() {
    return await axios.post(`technical/inspection/reprint`)
        .then(({data}) => {
            return data;
        }).catch(defaultError);
}

