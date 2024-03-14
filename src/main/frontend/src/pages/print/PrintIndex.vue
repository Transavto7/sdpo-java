<script>
import VueDatePicker from "@vuepic/vue-datepicker";

export default {
  components: {VueDatePicker},
  props: {
    inspections: {
      type: Array,
      require: true,
      default: []
    },
    driver : {
      type: Object,
      require: true,
    }
  },
  data() {
    return {
      date: new Date(),
    }
  },
  methods: {
    print(inspection) {
      this.$emit('print', inspection )
    },
    getDates() {
      let array = [];
      for (const value of JSON.parse(JSON.stringify(this.inspections))) {
        if ((new Date(value.created_at)).toDateString() === new Date(this.date).toDateString()) {
          array[value.created_at] = {
            date: value.created_at,
            type: 'line',
            color: 'red',
          };
        }
      }
      return array;
    }
  },
  watch: {},
  computed: {
    dateNow() {
      return new Date();
    },
    firstDayLastMonth() {
      let lastDate = new Date();
      lastDate.setMonth(lastDate.getMonth() - 1, 1);
      return lastDate
    },
    hasInspections() {
      return this.inspections.length > 0;
    },
  },
  mounted() {

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
                       disabled-times
                       locale="ru"
                       :hide-navigation="['month','time', 'hours', 'seconds']"
                       :min-date="firstDayLastMonth"
                       :max-date="dateNow"
                       prevent-min-max-navigation
        />
      </div>
      <div v-if="hasInspections" class="content-block__table">
        <table>
          <thead>
          <tr>
            <th>Дата Время</th>
            <th>Тип осмотра</th>
            <th></th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="inspection in this.inspections">
            <td>{{ inspection.created_at }}</td>
            <td>{{ inspection.type_view }}</td>
            <td><a class="btn icon" @click="print(inspection)"> <i class="ri-printer-fill"></i> </a></td>
          </tr>
          </tbody>
        </table>
      </div>
      <div v-if="!hasInspections" class="content-block__warning">
        <div class="driver-form__not-found animate__animated animate__fadeInUp">Ранее осмотры не проводились</div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>