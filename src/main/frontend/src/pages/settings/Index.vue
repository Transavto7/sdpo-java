<script>
import Testing from "./tabs/Testing.vue"
import Tonometer from "./tabs/Tonometer.vue";
import Password from "./tabs/Password.vue";
import MainSettings from "./tabs/MainSettings";
import Logo from "./tabs/Logo.vue";
import Print from "@/pages/settings/tabs/Print";
import {loadSettings} from "@/helpers/settings";

export default {
  components: {Print, Testing, Tonometer, Password, MainSettings, Logo},
  mounted() {
    loadSettings()
  },
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
      <button class="admin__tab animate__animated animate__fadeInDown d-1" @click="selected = 'main'"
              :class="{active: selected === 'main'}">
        Основные настройки
      </button>
      <button class="admin__tab animate__animated animate__fadeInDown d-2" @click="selected = 'password'"
              :class="{active: selected === 'password'}">
        Настройки пароля
      </button>
      <button class="admin__tab animate__animated animate__fadeInDown d-3" @click="selected = 'tonometer'"
              :class="{active: selected === 'tonometer'}">
        Настройки тонометра
      </button>
      <button class="admin__tab animate__animated animate__fadeInDown d-3" @click="selected = 'logo'"
              :class="{active: selected === 'logo'}">
        Смена логотипа
      </button>
      <button class="admin__tab animate__animated animate__fadeInDown d-4" @click="selected = 'testing'"
              :class="{active: selected === 'testing'}">
        Тестирование
      </button>
    </div>

    <testing v-if="selected === 'testing'"/>
    <tonometer v-if="selected === 'tonometer'"/>
    <password v-if="selected === 'password'"/>
    <main-settings v-if="selected === 'main'"/>
    <logo v-if="selected === 'logo'"/>
  </div>
</template>