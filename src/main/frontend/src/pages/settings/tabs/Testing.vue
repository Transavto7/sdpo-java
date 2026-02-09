<script>
import { makePhoto, makeVideoTest } from '@/helpers/camera';
import { getPressure, setTestValues as setTonometerTestValues } from '@/helpers/tonometer';
import { getTemp, setTestValue as setThermometerTestValue } from '@/helpers/thermometer';
import { getAlcometerResult, setTestValue as setAlcometerTestValue } from '@/helpers/alcometer';
import { print } from '@/helpers/printer';
import { useToast } from "vue-toastification";

export default {
    data() {
        return {
            show: '',
            image: null,
            video: null,
            temp: null,
            alcometerPpm: null,
            pressure: null,
            timeout: 0,
            loading: false,
            interval: null,
            toast: useToast(),
            customSystolic: 120,
            customDiastolic: 80,
            customPulse: 70,
            customAlcometer: 0.0,
            customThermometer: 36.6,
        }
    },
    methods: {
        async photo() {
            this.show = 'loading';
            clearInterval(this.interval);
            this.image = await makePhoto();
            this.show = '';
            if (this.image) {
                setTimeout(() => {
                    this.show = 'image';
                }, 2000);
            }
        },
        async video() {
            this.show = 'loading';
            clearInterval(this.interval);
            this.video = await makeVideoTest();
            this.show = '';

            if (this.video) {
                this.show = 'video';
            }
        },
        async thermometer() {
            this.show = 'loading';
            clearInterval(this.interval);
            this.interval = setInterval(async () => {
                this.temp = await getTemp();
                
                if (this.temp === 'next') {
                    return;
                }

                this.show = 'temp';
                clearInterval(this.interval);
            }, 1000);
            
        },
        async alcometer() {
           this.show = 'loading';
           clearInterval(this.interval);
           this.interval = setInterval(async () => {
                const result = await getAlcometerResult();

                if (result === 'next') {
                    return;
                }

                this.alcometerPpm = Number(result) || 0;
                this.show = 'alcometer';
                clearInterval(this.interval);
            }, 1000);
        },
        async printer() {
            this.show = 'loading';
            this.alcometerPpm = await print();
            this.show = '';
        },
        async tonometer() {
            this.show = 'loading';
            clearInterval(this.interval);
            this.interval = setInterval(async () => {
                const result = await getPressure();

                if (result === 'next') {
                    return;
                }

                this.pressure = result;

                clearInterval(this.interval);
                this.show = 'pressure';
            }, 1000);
        },
        async setTestTonometerValues(systolic, diastolic, pulse) {
            try {
                this.loading = true;
                const result = await setTonometerTestValues(systolic, diastolic, pulse);
                if (result && result.success) {
                    this.toast.success(`Установлены тестовые значения: ${systolic}/${diastolic}, пульс: ${pulse}`);
                } else {
                    this.toast.error('Ошибка установки тестовых значений');
                }
            } catch (error) {
                this.toast.error('Ошибка установки тестовых значений: ' + (error.response?.data?.error || error.message));
            } finally {
                this.loading = false;
            }
        },
        async setNormalValues() {
            await this.setTestTonometerValues(120, 80, 70);
        },
        async setHighPressure() {
            await this.setTestTonometerValues(160, 95, 85);
        },
        async setLowPulse() {
            await this.setTestTonometerValues(120, 80, 45);
        },
        async setHighPulse() {
            await this.setTestTonometerValues(120, 80, 120);
        },
        async setCustomValues() {
            await this.setTestTonometerValues(
                parseInt(this.customSystolic),
                parseInt(this.customDiastolic),
                parseInt(this.customPulse)
            );
        },
        // Алкометр
        async setTestAlcometerValue(value) {
            try {
                this.loading = true;
                const result = await setAlcometerTestValue(value.toString());
                if (result && result.success) {
                    this.toast.success(`Установлено тестовое значение алкометра: ${value} ‰`);
                } else {
                    this.toast.error('Ошибка установки тестового значения');
                }
            } catch (error) {
                this.toast.error('Ошибка установки тестового значения: ' + (error.response?.data?.error || error.message));
            } finally {
                this.loading = false;
            }
        },
        async setAlcometerZero() {
            await this.setTestAlcometerValue(0.0);
        },
        async setAlcometerLow() {
            await this.setTestAlcometerValue(0.3);
        },
        async setAlcometerHigh() {
            await this.setTestAlcometerValue(1.5);
        },
        async setCustomAlcometerValue() {
            await this.setTestAlcometerValue(parseFloat(this.customAlcometer));
        },
        // Термометр
        async setTestThermometerValue(temp) {
            try {
                this.loading = true;
                const result = await setThermometerTestValue(temp);
                if (result && result.success) {
                    this.toast.success(`Установлена тестовая температура: ${temp} °C`);
                } else {
                    this.toast.error('Ошибка установки тестовой температуры');
                }
            } catch (error) {
                this.toast.error('Ошибка установки тестовой температуры: ' + (error.response?.data?.error || error.message));
            } finally {
                this.loading = false;
            }
        },
        async setThermometerNormal() {
            await this.setTestThermometerValue(36.6);
        },
        async setThermometerHigh() {
            await this.setTestThermometerValue(37.5);
        },
        async setThermometerVeryHigh() {
            await this.setTestThermometerValue(38.5);
        },
        async setCustomThermometerValue() {
            await this.setTestThermometerValue(parseFloat(this.customThermometer));
        }
    }
}
</script>

<template>
    <div class="admin__testing">
        <button :disabled="show === 'loading'" @click="photo()" class="btn blue tab animate__animated animate__fadeInUp">Тестовый снимок</button>
        <button :disabled="show === 'loading'" @click="video()" class="btn blue tab animate__animated animate__fadeInUp d-1">Тестовое видео</button>
        <button :disabled="show === 'loading'" @click="thermometer()" class="btn blue tab animate__animated animate__fadeInUp d-2">Тест пирометра</button>
        <button :disabled="show === 'loading'" @click="alcometer()" class="btn blue tab animate__animated animate__fadeInUp d-3">Тест алкометра</button>
        <button :disabled="show === 'loading'" @click="printer()" class="btn blue tab animate__animated animate__fadeInUp d-4">Тестовая печать</button>
        <button :disabled="show === 'loading'" @click="tonometer()" class="btn blue tab animate__animated animate__fadeInUp d-5">Тест тонометра</button>

        <!-- Секция установки тестовых значений тонометра -->
        <div class="test-section animate__animated animate__fadeInUp d-6">
            <h3>Установка тестовых значений тонометра</h3>

            <div class="test-presets">
                <button :disabled="loading" @click="setNormalValues()" class="btn green">
                    Нормальные<br>
                    <small>120/80, пульс 70</small>
                </button>
                <button :disabled="loading" @click="setHighPressure()" class="btn orange">
                    Высокое давление<br>
                    <small>160/95, пульс 85</small>
                </button>
                <button :disabled="loading" @click="setLowPulse()" class="btn orange">
                    Низкий пульс<br>
                    <small>120/80, пульс 45</small>
                </button>
                <button :disabled="loading" @click="setHighPulse()" class="btn orange">
                    Высокий пульс<br>
                    <small>120/80, пульс 120</small>
                </button>
            </div>

            <div class="test-custom">
                <h4>Произвольные значения</h4>
                <div class="test-inputs">
                    <div class="input-group">
                        <label>Систолическое:</label>
                        <input type="number" v-model="customSystolic" min="50" max="250" />
                    </div>
                    <div class="input-group">
                        <label>Диастолическое:</label>
                        <input type="number" v-model="customDiastolic" min="30" max="150" />
                    </div>
                    <div class="input-group">
                        <label>Пульс:</label>
                        <input type="number" v-model="customPulse" min="30" max="200" />
                    </div>
                </div>
                <button :disabled="loading" @click="setCustomValues()" class="btn blue">
                    Установить произвольные значения
                </button>
            </div>
        </div>

        <!-- Секция установки тестовых значений алкометра -->
        <div class="test-section animate__animated animate__fadeInUp d-7">
            <h3>Установка тестовых значений алкометра</h3>

            <div class="test-presets">
                <button :disabled="loading" @click="setAlcometerZero()" class="btn green">
                    Трезв<br>
                    <small>0.0 ‰</small>
                </button>
                <button :disabled="loading" @click="setAlcometerLow()" class="btn orange">
                    Легкое опьянение<br>
                    <small>0.3 ‰</small>
                </button>
                <button :disabled="loading" @click="setAlcometerHigh()" class="btn red">
                    Сильное опьянение<br>
                    <small>1.5 ‰</small>
                </button>
            </div>

            <div class="test-custom">
                <h4>Произвольное значение</h4>
                <div class="test-inputs">
                    <div class="input-group">
                        <label>Алкоголь (‰):</label>
                        <input type="number" v-model="customAlcometer" min="0" max="5" step="0.1" />
                    </div>
                </div>
                <button :disabled="loading" @click="setCustomAlcometerValue()" class="btn blue">
                    Установить произвольное значение
                </button>
            </div>
        </div>

        <!-- Секция установки тестовых значений термометра -->
        <div class="test-section animate__animated animate__fadeInUp d-8">
            <h3>Установка тестовых значений термометра</h3>

            <div class="test-presets">
                <button :disabled="loading" @click="setThermometerNormal()" class="btn green">
                    Нормальная<br>
                    <small>36.6 °C</small>
                </button>
                <button :disabled="loading" @click="setThermometerHigh()" class="btn orange">
                    Повышенная<br>
                    <small>37.5 °C</small>
                </button>
                <button :disabled="loading" @click="setThermometerVeryHigh()" class="btn red">
                    Высокая<br>
                    <small>38.5 °C</small>
                </button>
            </div>

            <div class="test-custom">
                <h4>Произвольное значение</h4>
                <div class="test-inputs">
                    <div class="input-group">
                        <label>Температура (°C):</label>
                        <input type="number" v-model="customThermometer" min="30" max="45" step="0.1" />
                    </div>
                </div>
                <button :disabled="loading" @click="setCustomThermometerValue()" class="btn blue">
                    Установить произвольное значение
                </button>
            </div>
        </div>

        <div v-if="show === 'loading'" class="admin__loading animate__animated animate__fadeInUp">
            <div class="lds-ring"><div></div><div></div><div></div><div></div></div> 
            Загрузка
        </div>

        <div v-else class="admin__testing-result">
            <img class="animate__animated animate__fadeInUp" v-if="show === 'image'" :src="image">

            <div v-if="show === 'video'" class="admin__testing-video animate__animated animate__fadeInUp">
                <video autoplay="autoplay" controls>
                    <source src="http://localhost:8080/video" type="video/mp4">
                </video>
            </div>

            <div v-if="show === 'temp'" class="admin__testing-temp animate__animated animate__fadeInUp">
                {{ temp }} <span>°C</span>
            </div>

            <div v-if="show === 'alcometer'" class="admin__testing-temp animate__animated animate__fadeInUp">
                {{ alcometerPpm === undefined ? 'Не удалось получить результат' : alcometerPpm + ' ‰' }}
            </div>
            
            <div v-if="show === 'pressure'" class="admin__testing-pressure animate__animated animate__fadeInUp">
                <div class="admin__testing-pressure-item">
                    <span>Систолическое давление</span>
                    {{ pressure?.systolic ? pressure.systolic + ' мм. рт.ст' : 'Результатов нет' }}
                </div>
                <div class="admin__testing-pressure-item">
                    <span>Диастолическое давление</span>
                    {{ pressure?.diastolic ? pressure.diastolic + ' мм. рт.ст' : 'Результатов нет' }}
                </div>
                <div class="admin__testing-pressure-item">
                    <span>Пульс</span>
                    {{ pressure?.pulse ? pressure.pulse + ' уд/мин' : 'Результатов нет' }}
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">
.test-section {
    margin-top: 20px;
    padding: 15px;
    background-color: #f5f7fa;
    border-radius: 8px;

    h3 {
        margin: 0 0 10px 0;
        color: #3c495c;
        font-size: 1.1em;
        font-weight: 500;
    }

    h4 {
        margin: 0 0 8px 0;
        color: #3c495c;
        font-size: 0.95em;
        font-weight: 500;
    }
}

.test-presets {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
    gap: 10px;
    margin-bottom: 15px;

    button {
        padding: 8px 10px;
        min-height: 50px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        font-size: 0.9em;

        small {
            margin-top: 3px;
            font-size: 0.8em;
            opacity: 0.9;
        }
    }
}

.test-custom {
    background-color: white;
    padding: 12px;
    border-radius: 6px;
    border: 1px solid #dfe5ec;
}

.test-inputs {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
    gap: 10px;
    margin-bottom: 12px;
}

.input-group {
    display: flex;
    flex-direction: column;
    gap: 5px;

    label {
        color: #3c495c;
        font-size: 0.85em;
        font-weight: 500;
    }

    input[type="number"] {
        padding: 6px 8px;
        border: 1px solid #dfe5ec;
        border-radius: 4px;
        font-size: 0.9em;
        transition: border-color 0.2s;

        &:focus {
            outline: none;
            border-color: #4a90e2;
        }
    }
}

.test-custom {
    .btn {
        padding: 8px 15px;
        font-size: 0.9em;
    }
}

.btn {
    &.orange {
        background-color: #ff9800;

        &:hover:not(:disabled) {
            background-color: #f57c00;
        }
    }

    &.red {
        background-color: #e53935;

        &:hover:not(:disabled) {
            background-color: #c62828;
        }
    }
}
</style>