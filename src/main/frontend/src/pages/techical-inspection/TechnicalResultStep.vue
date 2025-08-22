<script>
import {
  getWishMessage
} from '@/helpers/api/api';
import ResultRepeat from "@/components/ResultRepeat";
import Loader from "@/components/common/Loader";
import {saveTechnicalInspection} from "@/helpers/api/technical";

export default {
  components: {Loader, ResultRepeat},
  data() {
    return {
      saved: false,
      conclusion: {
        admitted: '',
        comments: '',
      },
      notIdentified: 'Не идентифицирован',
      loading: false,
      feedback: null,
      phrase: '',
    }
  },
  async mounted() {
    if (!this.$store.state.technical.odometer) {
      this.$router.push({name: 'technical-odometer'});
    }

    this.$data.loading = true;
    await this.getWishMessage();
    this.$data.loading = false;
  },
  methods: {
    async getWishMessage() {
      this.phrase = (await getWishMessage()).wish_message ?? null;
    },
    async save() {
      this.$store.state.technical.point_reys_control = 'Пройден';

      await saveTechnicalInspection(this.$store.state.technical);
      await this.print();
      this.saved = true;
    },
    async print() {
      // await this.save();
      // await replayPrint();
      console.log('print')
    },
  },
  computed: {
    inspection() {
      return this.$store.state.technical;
    },
    car() {
      return this.$store.state.car ?? {};
    },
    drawReaction() {
      return this.phrase || "Хорошего дня!";
    },
    connection() {
      return this.$store.state.connection || false;
    },
    datetime() {
      const today = this.inspection.date ? new Date(Date.parse(this.inspection.date)) : new Date();
      const yyyy = today.getFullYear();
      let mm = today.getMonth() + 1;
      let dd = today.getDate();
      let HH = today.getHours();
      let MM = today.getMinutes();

      if (dd < 10) dd = '0' + dd;
      if (mm < 10) mm = '0' + mm;
      if (HH < 10) HH = '0' + HH;
      if (MM < 10) MM = '0' + MM;

      return dd + '.' + mm + '.' + yyyy + ', ' + HH + ':' + MM;
    }
  }
}
</script>

<template>
  <loader v-model:loading="loading"/>
  <div style="display: flex; ">
    <div v-if="!loading" class="step-result">
      <div style="text-align: center;">
        <h3 class="step-result__header_with_result animate__animated animate__fadeInDown">Проверьте введенные данные:</h3>
        <div class="step-result__cards">
          <div class="step-result__card animate__animated animate__fadeInUp d-1">
            <span>Идентификатор</span>
            <div class="step-result__card__info">{{ car.hashId }}</div>
          </div>
          <div class="step-result__card animate__animated animate__fadeInUp d-1">
            <span>Гос номер</span>
            {{ car.gosNumber }}
          </div>
          <div class="step-result__card animate__animated animate__fadeInUp d-1">
            <span>Марка</span>
            <div class="step-result__card__info">{{ car.markModel }}</div>
          </div>
          <div class="step-result__card animate__animated animate__fadeInUp d-1">
            <span>Показатель одометра</span>
            <div class="step-result__card__info">{{ inspection.odometer }}</div>
          </div>
          <div class="step-result__card animate__animated animate__fadeInUp d-1">
            <span>Дата, время ТО</span>
            <div class="step-result__card__info">{{ datetime }}</div>
          </div>
        </div>
      </div>
      <div class="step-result__footer">
        <div class="step-result__buttons">
          <button @click="$router.push('/')" class="btn blue animate__animated animate__fadeInUp">В начало</button>
          <button v-if="!this.saved" @click="save"
                  class="btn opacity animate__animated animate__fadeInUp">Сохранить и напечатать
          </button>
          <button v-if="this.saved" @click="save"
                  class="btn opacity animate__animated animate__fadeInUp">Печать
          </button>
        </div>
      </div>
    </div>
    <div v-if="!loading" class="madam-t7 animate__fadeInUpBig">
      <div class="madam-t7-text-box animate__animated animate__fadeInUp">
        <div class="wish">
          <span class="animate__fadeInUp"> {{ drawReaction }}</span>
        </div>
        <img width="300" src="@/assets/images/madam-t7-say.svg">
      </div>
      <img width="300" height="500" src="@/assets/images/madam-t7.svg">
    </div>
  </div>
</template>

<style lang="scss" scoped>
.step-result__card {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  font-size: 20px;
  font-weight: 500;
  background-color: rgba(#244673, 0.25);
  padding: 0 0 10px;
  border-radius: 10px;
  min-width: 200px;

  &__info {
    padding: 5px;
  }

  span {
    box-sizing: border-box;
    background-color: rgba(#244673, 0.9);
    color: #fff;
    padding: 5px 10px;
    margin-bottom: 10px;
    width: 100%;
    text-align: center;
    border-radius: 10px 10px 0 0;
  }
}

.footer__serial-number_date-notification {
  bottom: 10px;
  right: 20px;
}
</style>