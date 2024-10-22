<script>
import VueDatePicker from "@vuepic/vue-datepicker";

export default {
  components: {VueDatePicker},
  props: {
    inspections: {
      type: Object,
      require: true,
      default: () => {
        return {};
      }
    },
    driver: {
      type: Object,
      require: true,
    }
  },
  data() {
    return {
      date: new Date(),
      inspectionsFiltered: [],
      dateFiltered: []
    }
  },
  methods: {
    getTimeOnly(data) {
      return (new Date(data).toTimeString()).split(' ')[0];
    },
    print(inspection) {
      this.$emit('print', inspection)
    },
    printQr(inspection) {
      this.$emit('printQr', inspection)
    },
    initDateRange() {
      if (this.hasInspections) {
        for (const value of (this.inspections)) {
          this.dateFiltered.push({
            date: value.created_at,
            type: 'line',
            color: 'red',
          });
        }
      }
    }
  },
  watch: {
    date() {
      this.inspectionsFiltered = [];
      if (this.hasInspections) {
        for (const value of (this.inspections)) {
          if ((new Date(value.created_at)).toDateString() === new Date(this.date).toDateString()) {
            this.inspectionsFiltered.push(value);
          }
        }
      }
    }
  },
  computed: {
    connection() {
      return this.$store.state.connection || false;
    },
    dateNow() {
      return new Date();
    },
    firstDayLastMonth() {
      let lastDate = new Date();
      lastDate.setMonth(lastDate.getMonth() - 1, 1);
      return lastDate
    },
    hasSelectInspections() {
      return this.inspectionsFiltered.length > 0;
    },
    hasInspections() {
      return this.inspections.length > 0;
    },
  },
  mounted() {
    this.initDateRange()
    this.date = this.dateNow;
  },
  unmounted() {
  }
}
</script>

<template>
  <div class="last-inspection">
    <div class="last-inspection__header">
      <h1>{{ driver.fio }}</h1>
    </div>
    <div class="last-inspection_body">
      <div class="content-block__calendar">
        <VueDatePicker v-model="date"
                       inline
                       auto-apply
                       :disable-year-select="true"
                       locale="ru"
                       :hide-navigation="['month','time', 'hours', 'seconds']"
                       :min-date="firstDayLastMonth"
                       :max-date="dateNow"
                       prevent-min-max-navigation
                       :markers="dateFiltered"
        />
      </div>
      <div v-if="hasSelectInspections" class="content-block__table">
        <table>
          <thead>
          <tr>
            <th style="width: 40%;">Дата Время</th>
            <th>Тип осмотра</th>
            <th></th>
            <th></th>
          </tr>
          </thead>
          <tbody class=" animate__animated animate__fadeInUp">
          <tr class=" animate__animated animate__fadeInUp"
              v-for="inspection in this.inspectionsFiltered">
            <td class="time">{{ getTimeOnly(inspection.created_at) }}</td>
            <td>{{ inspection.type_view }}</td>
            <td><a class="btn icon" @click="print(inspection)"> <i class="ri-printer-fill"></i> </a></td>
            <td><a class="btn icon" v-if="connection" @click="printQr(inspection)"> <i class="ri-qr-code-line"></i> </a></td>
          </tr>
          </tbody>
        </table>
      </div>
      <div v-if="!hasSelectInspections" class="content-block__warning">
        <div class="driver-form__not-found animate__animated animate__fadeInUp">Осмотры на выбранную дату не
          проводились
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>