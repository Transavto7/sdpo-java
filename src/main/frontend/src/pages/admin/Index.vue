<script>
import Testing from "./Testing.vue"
import Tonometer from "./Tonometer.vue";

export default {
    components: { Testing, Tonometer },
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
            <button class="admin__tab" @click="selected = 'main'" :class="{active: selected === 'main'}">
                Основные настройки
            </button>
            <button class="admin__tab" @click="selected = 'password'" :class="{active: selected === 'password'}">
                Настройки пароля
            </button>
            <button class="admin__tab" @click="selected = 'tonometer'" :class="{active: selected === 'tonometer'}">
                Настройки танометра
            </button>
            <button class="admin__tab" @click="selected = 'testing'" :class="{active: selected === 'testing'}">
                Тестирование
            </button>
        </div>

        <testing v-if="selected === 'testing'" />
        <tonometer v-if="selected === 'tonometer'" />
    </div>
</template>