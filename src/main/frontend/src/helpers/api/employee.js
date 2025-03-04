import store from "@/store";
import axios from "axios";
import {defaultError} from "@/helpers/http-errors";
import {useToast} from "vue-toastification";

const toast = useToast();

export async function getEmployee(id) {
    return await axios.post(`employees/inspection/${id}`)
        .then(({data}) => {
        return data;
    });
}

export async function saveEmployeeInspection(inspection = store.state.inspection) {
    console.log("SAVE", inspection);
    // return await axios.post(`employees/inspection/save`, inspection)
    //     .then(({data}) => {
    //     return data;
    // }).catch(defaultError);
}

