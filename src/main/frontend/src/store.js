import { createStore } from 'vuex';
import { useToast } from "vue-toastification";

const toast = useToast();
const store = createStore({
    state() {
        return {
            inspection: {},
            driver: {},
            config: {},
            admin: false,
            connection: false,
            point: '',
            verification: {
                serialNumberTerminal : '',
                dateInspection: '',
            },
            selectingMedic: false,
            loseConnect: false,
            waitRecordMedia: false,
            timerRecordMedia: null,
            temp: {},
        }
    },
    getters: {
        url: state => {
            if(state.config?.main?.url) {
                return state.config.main.url;
            } else {
                toast.error('Ошибка конфигурации. URL адрес не указан.')
                throw new Error('Error configuration API url');
            }
        }
    }
});

export default store;