<script>
export default {
    data() {
        return {
        }
    },
    computed: {
        inspection() {
            return this.$store.state.inspection;
        }
    }
}
</script>

<template>
    <div class="step-3__outer">
        <div class="step-3">
            <h3>Измерение артериального давления</h3>
            <div class="step-3__items">
                <div class="step-3__item">
                    <span>1</span>
                    Закрепите манжету тонометра на левом плече на 2-3 см выше сгиба локтя
                </div>
                <div class="step-3__item">
                    <span>2</span>
                    Положите руку на стол
                </div>
                <div class="step-3__item">
                    <span>3</span>
                    Убедитесь, что манжета находится на уровне сердца
                </div>
                <div class="step-3__item">
                    <span>4</span>
                    Нажмите кнопку СТАРТ на тонометре
                </div>
            </div>

            <div class="step-3__subtext">
                Не двигайтесь, пока идет измерение.<br>
                После окончания измерения снимите манжету
            </div>
        </div>

        <div class="step-buttons">
            <button @click="$router.push('/step/2')" class="btn opacity blue">Назад</button>
            <button @click="$router.push('/step/4')" v-if="this.$store.state.inspection.type_view" class="btn">Продолжить</button>
        </div>
    </div>
</template>