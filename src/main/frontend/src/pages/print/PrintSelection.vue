<script>

export default {
  methods: {
    selectBus() {
      this.$emit('select', 'carId');
    },
    selectDriver() {
      this.$emit('select', 'driverId');
    },
    selectInspection() {
      this.$emit('select', 'inspection');
    }
  }
}
</script>

<template>
  <div class="print-selection">
    <div class="tab animate__animated animate__fadeInUp d-1" @click="selectBus()">
      <img width="300" src="@/assets/images/print/busQR.svg">
      <label style="margin-top: 15px;">QR испекции автомобиля</label>
    </div>
    <div class="tab">
      <img width="300" src="@/assets/images/print/driverQR.svg" @click="selectDriver()">
      <label style="margin-top: 15px;">QR осмотра водителя</label>
    </div>
    <div class="tab">
      <img style="margin-bottom: 15px;margin-top: 15px;" width="250" src="@/assets/images/print/inspection.svg" @click="selectInspection()">
      <label>Заключение медицинского осмотра</label>
    </div>
  </div>
</template>

<style scoped>

</style>