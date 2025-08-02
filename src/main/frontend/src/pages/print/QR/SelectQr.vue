<script>

export default {
  data(){
    return {
      showPayments: false,
    }
  },
  methods: {
    selectBus() {
      this.$emit('select', 'carId');
    },
    selectDriver() {
      this.$emit('select', 'driverId');
    },
    selectBank() {
      this.showPayments = true;
    },
  }
}
</script>

<template>
  <div>
    <div v-if="!showPayments" class="print-selection">
      <div class="tab animate__animated animate__fadeInUp d-1" @click="selectBus()">
        <img width="250" src="@/assets/images/print/busQR.svg">
        <label style="margin-top: 15px;">QR инспекции автомобиля</label>
      </div>
      <div class="tab animate__animated animate__fadeInUp d-1" @click="selectDriver()">
        <img width="250" src="@/assets/images/print/driverQR.svg">
        <label style="margin-top: 15px;">QR осмотра водителя</label>
      </div>
      <div class="tab animate__animated animate__fadeInUp d-1" @click="selectBank()">
        <img width="250" src="@/assets/images/print/bankQR.svg">
        <label style="margin-top: 15px;">Оплата услуг</label>
      </div>
    </div>
    <div v-if="showPayments" class="payment-show">
      <img height="500" alt="QR не найден обратитесь к администратору" src="@/assets/images/print/payment_qr.webp">
    </div>
  </div>
</template>

<style scoped lang="scss">
.payment-show {
  display: flex;
  flex-wrap: wrap;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  font-weight: 500;
  gap: 1.3rem;
}
</style>