import store from '@/store';
import axios from 'axios';

export async function loadSettings() {
    await axios.post('/setting/load').then(({ data }) => {
        store.state.config = data;
    }).catch(error => {
        console.log(error);
    })
}

export async function savePassword(password) {
    await axios.post('/setting/password', {
        password
    }).catch(error => {
        console.log(error);
    });
}