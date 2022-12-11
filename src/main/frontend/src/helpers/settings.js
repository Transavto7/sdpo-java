import store from '@/store';

export async function loadSettings() {
    await axios.post('/setting/load').then(({ data }) => {
        store.state.config = data;
    }).then(error => {
        console.log(error);
    })
}