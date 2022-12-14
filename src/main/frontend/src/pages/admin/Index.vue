<script>
import Testing from "./Testing.vue"
import Tonometer from "./Tonometer.vue";
import Password from "./Password.vue";

export default {
    components: { Testing, Tonometer, Password },
    data() {
        return {
            selected: 'main',
        }
    }
}
</script>

<template>
    <div class="admin">
        <div class="admin__tabs">
            <button class="admin__tab animate__animated animate__fadeInDown d-1" @click="selected = 'main'" :class="{active: selected === 'main'}">
                Основные настройки
            </button>
            <button class="admin__tab animate__animated animate__fadeInDown d-2" @click="selected = 'password'" :class="{active: selected === 'password'}">
                Настройки пароля
            </button>
            <button class="admin__tab animate__animated animate__fadeInDown d-3" @click="selected = 'tonometer'" :class="{active: selected === 'tonometer'}">
                Настройки танометра
            </button>
            <button class="admin__tab animate__animated animate__fadeInDown d-4" @click="selected = 'testing'" :class="{active: selected === 'testing'}">
                Тестирование
            </button>
        </div>

        <testing v-if="selected === 'testing'" />
        <tonometer v-if="selected === 'tonometer'" />
        <password  v-if="selected === 'password'" />
    </div>
</template>