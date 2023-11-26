import { createStore } from 'vuex';
import { useToast } from "vue-toastification";

const toast = useToast();
const store = createStore({
    state() {
        return {
            logs: [],
            inspection: {},
            driver: {},
            config: {},
            admin: false,
            connection: false,
            point: '',
            selectingMedic: false,
            loseConnect: false,
        }
    },
    mutations: {
        PUSH_LOG (state, log) {
            state.logs.push(log)
        },
    },
    actions: {
        pushLog ({ commit }, log) {
            const logStr = `[${new Date().toISOString()}] ${log}`
            commit('PUSH_LOG', logStr)
        },
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