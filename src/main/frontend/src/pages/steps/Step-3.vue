<script>
export default {
    data() {
        return {
        }
    },
    computed: {
        inspection() {
            return this.$store.state.inspection;
        },
        system() {
            return this.$store.state.config?.system || {};
        }
    }
}
</script>

<template>
    <div class="step-3__outer">
        <div class="step-3">
            <h3 class="animate__animated animate__fadeInDown">Измерение артериального давления</h3>
            <div class="step-3__items">
                <div class="step-3__item animate__animated animate__fadeInDown d-1">
                    <span>1</span>
                    Закрепите манжету тонометра на левом плече на 2-3 см выше сгиба локтя
                </div>
                <div class="step-3__item animate__animated animate__fadeInDown d-2">
                    <span>2</span>
                    Положите руку на стол
                </div>
                <div class="step-3__item animate__animated animate__fadeInDown d-3">
                    <span>3</span>
                    Убедитесь, что манжета находится на уровне сердца
                </div>
                <div class="step-3__item animate__animated animate__fadeInDown d-4">
                    <span>4</span>
                    Нажмите кнопку СТАРТ на тонометре
                </div>
            </div>

            <div class="step-3__subtext animate__animated animate__fadeInDown d-5">
                Не двигайтесь, пока идет измерение.<br>
                После окончания измерения снимите манжету
            </div>
        </div>

        <div class="step-buttons">
            <button @click="$router.push('/step/2')" class="btn opacity blue">Назад</button>
            <button @click="$router.push('/step/4')" v-if="JSON.parse(system.tonometer_skip)" class="btn">Продолжить</button>
        </div>
    </div>
</template>