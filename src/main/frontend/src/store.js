import { createStore } from 'vuex';
import { useToast } from "vue-toastification";

const toast = useToast();
const store = createStore({
    state() {
        return {
            inspection: {},
            technical: {},
            car: null,
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
            version: 'Загрузка...'
        }
    },
    getters: {
        url: state => {
            if(state.config?.connection?.url) {
                return state.config.connection.url;
            } else {
                toast.error('Ошибка конфигурации. URL адрес не указан.')
                throw new Error('Error configuration API url');
            }
        }
    }
});

export default store;