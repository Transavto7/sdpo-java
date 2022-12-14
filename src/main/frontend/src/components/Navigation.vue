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
        }
    }
}
</script>

<template>
    <div class="nav">
        <div class="nav__logo animate__animated animate__fadeInDown">
            <img src="@/assets/images/logo.png" alt="">
        </div>

        <div class="nav__buttons" v-if="['/help', '/login'].includes(currentRouter) || currentRouter.includes('/step/')">
            <button @click="$router.push('/')" class="btn opacity blue animate__animated animate__fadeInDown d-1">В начало</button>
        </div>

        <div class="nav__buttons" v-else-if="currentRouter.includes('/admin')">
            <button @click="logout()" class="btn opacity blue animate__animated animate__fadeInDown d-1">Выйти</button>
        </div>

        <div class="nav__buttons" v-else>
            <button class="btn opacity animate__animated animate__fadeInDown d-1">Мед работник</button>
            <button @click="$router.push('/help')" class="btn opacity animate__animated animate__fadeInDown d-2">Помощь</button>
            <button @click="$router.push('/login')" class="btn icon animate__animated animate__fadeInDown d-3">
                <i class="ri-tools-fill"></i>
            </button>
        </div>
    </div>
    <div class="step-progress" v-if="currentRouter.includes('/step/')">
        <div class="step-progress__item" v-for="i in 8" :key="i" :class="{active: pageNumber >= i }">
            <i class="ri-check-fill"></i>
        </div>
    </div>
</template>