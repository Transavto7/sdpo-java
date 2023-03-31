<script>
import { close } from '@/helpers/api';
export default {
    data() {
        return {

        }
    },
    methods: {
        close,
        requestVisibleMouse() {
            if (!document.documentElement.classList.contains('disable-mouse')) {
                document.documentElement.classList.add('disable-mouse');
                this.system.cursor = false;
            } else {
                document.documentElement.classList.remove('disable-mouse');
                this.system.cursor = true;
            }
        },
        requestFullScreen() {
            const isInFullScreen = (document.fullscreenElement && document.fullscreenElement !== null) ||
                (document.webkitFullscreenElement && document.webkitFullscreenElement !== null) ||
                (document.mozFullScreenElement && document.mozFullScreenElement !== null) ||
                (document.msFullscreenElement && document.msFullscreenElement !== null);

            const docElm = document.documentElement;
            if (!isInFullScreen) {
                if (docElm.requestFullscreen) {
                    docElm.requestFullscreen();
                } else if (docElm.mozRequestFullScreen) {
                    docElm.mozRequestFullScreen();
                } else if (docElm.webkitRequestFullScreen) {
                    docElm.webkitRequestFullScreen();
                } else if (docElm.msRequestFullscreen) {
                    docElm.msRequestFullscreen();
                }
            } else {
                if (document.exitFullscreen) {
                    document.exitFullscreen();
                } else if (document.webkitExitFullscreen) {
                    document.webkitExitFullscreen();
                } else if (document.mozCancelFullScreen) {
                    document.mozCancelFullScreen();
                } else if (document.msExitFullscreen) {
                    document.msExitFullscreen();
                }
            }
        },
    },
    computed: {
        currentRouter() {
            return this.$route.path;
        },
        pageNumber() {
            console.log(this.$route.meta?.number);
            return this.$route.meta?.number || 0;
        },
        logout() {
            this.$store.state.admin = false;
            this.$router.push('/');
        },
        system() {
            return this.$store.state.config?.system || {};
        },
        inspection() {
            return this.$store.state.inspection;
        },
        logo() {
            return this.$store.state.config?.main?.logo;
        }
    },
}
</script>

<template>
    <div class="nav">
        <div class="nav__logo animate__animated animate__fadeInDown">
            <img v-if="logo" :src="logo" alt="">
            <img v-else src="@/assets/images/logo.png" alt="">
        </div>
        
        <div v-if="currentRouter.includes('/step/')" class="nav__info animate__animated animate__fadeInDown">
            {{ inspection.driver_fio }}
            <span>{{ inspection.driver_id }}</span>
        </div>

        <div class="nav__buttons" v-if="['/help', '/login'].includes(currentRouter) || currentRouter.includes('/step/')">
            <button @click="$router.push('/')" class="btn opacity blue animate__animated animate__fadeInDown d-1">В начало</button>
        </div>

        <div class="nav__buttons" v-else-if="currentRouter.includes('/admin')">
            <button @click="requestVisibleMouse()" class="btn icon animate__animated animate__fadeInDown d-1" style="padding: 5px">
                <i class="ri-cursor-fill"></i>
            </button>
            <button @click="requestFullScreen()" class="btn icon animate__animated animate__fadeInDown d-1" style="padding: 5px">
                <i class="ri-fullscreen-line"></i>
            </button>
            <button @click="logout()" class="btn opacity blue animate__animated animate__fadeInDown d-2">Выйти</button>
        </div>

        <div class="nav__buttons" v-else>
            <button @click="$store.state.selectingMedic = true" class="btn opacity animate__animated animate__fadeInDown">Мед работник</button>
            <button @click="$router.push('/help')" class="btn opacity animate__animated animate__fadeInDown d-2">Помощь</button>
            <button @click="$router.push('/login')" class="btn icon animate__animated animate__fadeInDown d-3">
                <i class="ri-tools-fill"></i>
            </button>
        </div>
    </div>
    <div class="step-progress animate__animated animate__fadeInDown" v-if="currentRouter.includes('/step/')">
        <div class="step-progress__item" v-if="JSON.parse(system.driver_info)" :class="{active: pageNumber >= 1 }">
            <i class="ri-check-fill"></i>
        </div>
        <div class="step-progress__item" v-if="JSON.parse(system.type_ride)" :class="{active: pageNumber >= 2 }">
            <i class="ri-check-fill"></i>
        </div>
        <div class="step-progress__item" v-if="JSON.parse(system.type_ride)" :class="{active: pageNumber >= 3 }">
            <i class="ri-check-fill"></i>
        </div>
        <div class="step-progress__item" v-if="JSON.parse(system.tonometer_visible)" :class="{active: pageNumber >= 4 }">
            <i class="ri-check-fill"></i>
        </div>
        <div class="step-progress__item" v-if="JSON.parse(system.thermometer_visible)" :class="{active: pageNumber >= 5 }">
            <i class="ri-check-fill"></i>
        </div>
        <div class="step-progress__item" v-if="JSON.parse(system.alcometer_visible)" :class="{active: pageNumber >= 6 }">
            <i class="ri-check-fill"></i>
        </div>
        <div class="step-progress__item" v-if="JSON.parse(system.question_sleep)" :class="{active: pageNumber >= 7 }">
            <i class="ri-check-fill"></i>
        </div>
        <div class="step-progress__item" v-if="JSON.parse(system.question_helth)" :class="{active: pageNumber >= 8 }">
            <i class="ri-check-fill"></i>
        </div>
        <div class="step-progress__item" :class="{active: pageNumber >= 9 }">
            <i class="ri-check-fill"></i>
        </div>
    </div>
</template>