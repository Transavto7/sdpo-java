<script>
export default {
    data() {
        return {

        }
    },
    computed: {
        currentRouter() {
            return this.$route.path;
        },
        pageNumber() {
            const split = this.$route.path.split('/');
            return split[split.length - 1];
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
        }
    },
}
</script>

<template>
    <div class="nav">
        <div class="nav__logo animate__animated animate__fadeInDown">
            <img src="@/assets/images/logo.png" alt="">
        </div>
        
        <div v-if="currentRouter.includes('/step/')" class="nav__info animate__animated animate__fadeInDown">
            {{ inspection.driver_fio }}
            <span>{{ inspection.driver_id }}</span>
        </div>

        <div class="nav__buttons" v-if="['/help', '/login'].includes(currentRouter) || currentRouter.includes('/step/')">
            <button @click="$router.push('/')" class="btn opacity blue animate__animated animate__fadeInDown d-1">В начало</button>
        </div>

        <div class="nav__buttons" v-else-if="currentRouter.includes('/admin')">
            <button @click="logout()" class="btn opacity blue animate__animated animate__fadeInDown d-1">Выйти</button>
        </div>

        <div class="nav__buttons" v-else>
            <button class="btn opacity animate__animated animate__fadeInDown">Мед работник</button>
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
        <div class="step-progress__item" v-if="JSON.parse(system.tonometer_visible)" :class="{active: pageNumber >= 3 }">
            <i class="ri-check-fill"></i>
        </div>
        <div class="step-progress__item" v-if="JSON.parse(system.thermometer_visible)" :class="{active: pageNumber >= 4 }">
            <i class="ri-check-fill"></i>
        </div>
        <div class="step-progress__item" v-if="JSON.parse(system.alcometer_visible)" :class="{active: pageNumber >= 5 }">
            <i class="ri-check-fill"></i>
        </div>
        <div class="step-progress__item" v-if="JSON.parse(system.question_sleep)" :class="{active: pageNumber >= 6 }">
            <i class="ri-check-fill"></i>
        </div>
        <div class="step-progress__item" v-if="JSON.parse(system.question_helth)" :class="{active: pageNumber >= 7 }">
            <i class="ri-check-fill"></i>
        </div>
        <div class="step-progress__item" :class="{active: pageNumber >= 8 }">
            <i class="ri-check-fill"></i>
        </div>
    </div>
</template>