<script>
import { getTemp } from '@/helpers/thermometer';

export default {
    data() {
        return {
            interval: null,
        }
    },
    async mounted() {
        this.interval = setInterval(async () => {
            const result = await getTemp();

            if (result === undefined || result === null) {
                return;
            }

            if (result === 'next') {
                return;
            }

            this.inspection.t_people = Number(result) || 36.6;
            clearInterval(this.interval);
            this.$router.push({ name: 'step-alcometer' });
        }, 1000);
       
    },
    unmounted() {
        clearInterval(this.interval);
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
    <div class="step-4__outer">
        <div class="step-4">
            <h3 class="animate__animated animate__fadeInUp">Измерение температуры тела</h3>
            <img class="animate__animated animate__fadeInUp d-1" src="@/assets/images/pirometer2.png">
        </div>

        <div class="step-buttons">
            <button @click="$router.push({ name: 'step-tonometer' })" class="btn opacity blue">Назад</button>
            <button @click="$router.push({ name: 'step-alcometer' })" 
                    v-if="JSON.parse(system.thermometer_skip)" class="btn">Продолжить</button>
        </div>
    </div>
</template>