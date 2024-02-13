<script>
import Navigation from './components/Navigation.vue'
import ErrorModal from './components/ErrorModal.vue'

export default {
  name: 'App',
  components: {
    Navigation,
    ErrorModal
  },
  computed: {
    point() {
      return this.$store.state.point;
    },
    dateVerification() {
      return this.$store.state.verification.dateInspection
    },
    serialNumber() {
      return this.$store.state.verification.serialNumberTerminal
    },
    needVerification() {
      if (!this.dateVerification) return false;

      let now = new Date();
      let need = new Date(this.dateVerification | '')
      return (need - now) < 0
    },
    hasMonthIntoVerification() {
      if (!this.dateVerification) return false;

      let now = new Date();
      let need = new Date(this.dateVerification | '')
      return (need - now) < (1000 * 60 * 60 * 24 * 30) && (need > now)
    },
    title() {
      return !this.hasMonthIntoVerification ? 'Обратитесь к администратору до поверки осталось меньше месяца' : '';
    }
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
      <span>СДПО 2.5.7</span>
    </div>
  </div>
  <div class="footer__serial-number_date-notification">
    <div>
      <span :hidden="{needVerification}" class="danger">НЕОБХОДИМО ПРОЙТИ ПОВЕРКУ</span>
      <span :class="{danger : hasMonthIntoVerification}" :hidden="{hasMonthIntoVerification}" class="date-notification">До поверки устройства осталось меньше месяца</span>
      <span class="date-notification">{{dateVerification || ''}}</span>
    </div>
    <span class="serial-number">{{(serialNumber ? 'S/N ' + serialNumber : '') }}</span>
  </div>
</template>

<style>
</style>
