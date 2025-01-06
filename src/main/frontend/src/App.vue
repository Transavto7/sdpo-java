<script>
import Navigation from './components/navigation/Navigation.vue'
import ErrorModal from './components/ErrorModal.vue'
import store from "@/store";

export default {
  name: 'App',
  components: {
    Navigation,
    ErrorModal
  },
  computed: {
    connection() {
      return store.state.connection;
    },
    inspectionLeft() {
      let left = this.system.max_inspection_in_offline_mod - this.system.count_inspections;

      return left > 0 ? left : 0;
    },
    dayLeft() {
      let lastOnline = this.system.last_online || new Date();
      return this.system.delay_day_in_offline_mod - Math.floor(((new Date()) - Date.parse(lastOnline)) / 8.64e7);
    },
    system() {
      return this.$store.state.config?.system || {};
    },
    point() {
      return this.$store.state.point;
    },
    dateVerification() {
      return this.$store.state.verification.dateInspection
    },
    serialNumber() {
      return this.$store.state.verification.serialNumberTerminal
    },
  },
}
</script>

<template>
  <navigation/>
  <error-modal/>
  <transition name="fade">
    <router-view></router-view>
  </transition>
  <div class="footer">
    <div class="footer__point-version">
      {{ point || 'Незвестный пункт выпуска' }}
      <span>Версия: 2.8.4</span>
    </div>
  </div>
  <div class="footer__serial-number_date-notification">
    <div v-if="!connection">
      <span  class="date-notification" style="color: red">{{ 'Осталось ' + dayLeft + " день(дней) и " + inspectionLeft  + " осмотр(ов) в автономном режиме" }}</span>
    </div>
    <div v-else>
      <div>
        <span class="date-notification">{{dateVerification || ''}}</span>
      </div>
      <span class="serial-number">{{(serialNumber ? 'S/N ' + serialNumber : '') }}</span>
    </div>

  </div>
</template>

<style>
</style>
