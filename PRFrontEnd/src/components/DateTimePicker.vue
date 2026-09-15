<template>
  <div class="date-time-picker" tabindex="0" @focus="handleRootFocus">
    <div class="input-container" @click="handleContainerClick">
      <!-- 组合输入框 -->
      <div class="combo-input">
        <!-- 年 -->
        <input
            type="text"
            v-model="inputParts.year"
            @input="handleComboInput('year', $event)"
            @keyup="handleComboKeyup('year', $event)"
            @click="handleInputClick('year', $event)"
            @focus="handleComboFocus('year')"
            maxlength="4"
            class="combo-input-part combo-input-year"
            ref="yearInput"
            placeholder="----"
        >
        <span class="combo-input-separator">/</span>
        <!-- 月 -->
        <input
            type="text"
            v-model="inputParts.month"
            @input="handleComboInput('month', $event)"
            @keyup="handleComboKeyup('month', $event)"
            @click="handleInputClick('month', $event)"
            @focus="handleComboFocus('month')"
            maxlength="2"
            class="combo-input-part combo-input-month"
            ref="monthInput"
            placeholder="--"
        >
        <span class="combo-input-separator">/</span>
        <!-- 日 -->
        <input
            type="text"
            v-model="inputParts.day"
            @input="handleComboInput('day', $event)"
            @keyup="handleComboKeyup('day', $event)"
            @click="handleInputClick('day', $event)"
            @focus="handleComboFocus('day')"
            maxlength="2"
            class="combo-input-part combo-input-day"
            ref="dayInput"
            placeholder="--"
        >
        <span class="combo-input-separator"> </span>
        <!-- 时 -->
        <input
            type="text"
            v-model="inputParts.hour"
            @input="handleComboInput('hour', $event)"
            @keyup="handleComboKeyup('hour', $event)"
            @click="handleInputClick('hour', $event)"
            @focus="handleComboFocus('hour')"
            maxlength="2"
            class="combo-input-part combo-input-hour"
            ref="hourInput"
            placeholder="--"
        >
        <span class="combo-input-separator">:</span>
        <!-- 分 -->
        <input
            type="text"
            v-model="inputParts.minute"
            @input="handleComboInput('minute', $event)"
            @keyup="handleComboKeyup('minute', $event)"
            @click="handleInputClick('minute', $event)"
            @focus="handleComboFocus('minute')"
            maxlength="2"
            class="combo-input-part combo-input-minute"
            ref="minuteInput"
            placeholder="--"
        >
        <!-- 保留原生日历图标 -->
        <input
            type="datetime-local"
            :value="localValue"
            @input="handleNativeInput"
            :min="min"
            :max="max"
            class="combo-input-native"
            ref="nativeInput"
        >
      </div>
      <!-- 透明遮罩，与input同级 -->
      <div class="input-mask" @click="togglePicker"></div>
    </div>

    <!-- 自定义日期时间选择弹窗 -->
    <div v-if="showPicker" ref="pickerPopup" tabindex="-1" class="picker-popup" :class="{'picker-popup-top': popupPosition === 'top'}" @keydown="handlePopupKeydown" @click.stop>
      <div class="picker-header">
        <button @click="prevMonth">‹</button>
        <h3>{{ currentYear }}年{{ currentMonth + 1 }}月</h3>
        <button @click="nextMonth">›</button>
      </div>

      <!-- 星期标题 -->
      <div class="weekdays">
        <div v-for="day in weekdays" :key="day" class="weekday">{{ day }}</div>
      </div>

      <!-- 日期网格 -->
      <div class="calendar-grid">
        <!-- 上个月的日期 -->
        <div v-for="day in prevMonthDays" :key="'prev-' + day" class="calendar-day other-month">
          {{ day }}
        </div>

        <!-- 当前月的日期 -->
        <div
            v-for="day in currentMonthDays"
            :key="'current-' + day"
            class="calendar-day"
            :class="{
            'selected': isSelected(day),
            'today': isToday(day),
            'disabled': isDisabled(day),
            'highlighted': isHighlighted(day)
          }"
            @click="selectDate(day)"
        >
          {{ day }}
        </div>

        <!-- 下个月的日期 -->
        <div v-for="day in nextMonthDays" :key="'next-' + day" class="calendar-day other-month">
          {{ day }}
        </div>
      </div>

      <!-- 时间选择 -->
      <div class="time-selector">
        <div class="time-input">
          <label>时:</label>
          <select v-model="selectedHour" @change="updateDateTime" ref="popupHourSelect" @keydown="handleTimeSelectKeydown('hour', $event)">
            <option
                v-for="h in 24"
                :key="h - 1"
                :value="(h - 1).toString().padStart(2, '0')"
                :disabled="isHourDisabled(h - 1)"
            >
              {{ (h - 1).toString().padStart(2, '0') }}
            </option>
          </select>
        </div>
        <div class="time-input">
          <label>分:</label>
          <select v-model="selectedMinute" @change="updateDateTime" ref="popupMinuteSelect" @keydown="handleTimeSelectKeydown('minute', $event)">
            <option
                v-for="m in 60"
                :key="m - 1"
                :value="(m - 1).toString().padStart(2, '0')"
                :disabled="isMinuteDisabled(m - 1)"
            >
              {{ (m - 1).toString().padStart(2, '0') }}
            </option>
          </select>
        </div>
      </div>

      <!-- 错误提示 -->
      <div v-if="errorMessage" style="color: red; font-size: 12px; text-align: center; padding: 5px 10px;">
        {{ errorMessage }}
      </div>

      <!-- 操作按钮 -->
      <div class="picker-footer">
        <button @click="reset">重置</button>
        <button @click="confirm">确认</button>
      </div>
    </div>

    <!-- 点击外部关闭弹窗 -->
    <div v-if="showPicker" class="backdrop" @click="closePicker"></div>
  </div>
</template>

<script>
export default {
  name: 'DateTimePicker',
  props: {
    value: {
      type: String,
      default: ''
    },
    min: {
      type: String,
      default: ''
    },
    max: {
      type: String,
      default: ''
    },
    defaultToday: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      showPicker: false,
      currentDate: new Date(),
      selectedDate: null,
      selectedHour: '00',
      selectedMinute: '00',
      weekdays: ['日', '一', '二', '三', '四', '五', '六'],
      // 组合输入框各部分的值
      inputParts: {
        year: '',
        month: '',
        day: '',
        hour: '',
        minute: ''
      },
      // 错误提示
      errorMessage: '',
      // 标记是否刚刚通过defaultToday填入了默认时间
      justDefaultedToday: false,
      // 弹窗显示位置：bottom在输入框下方，top在输入框上方
      popupPosition: 'bottom',
      // 日历键盘导航的高亮日期
      highlightedDate: null
    };
  },
  computed: {
    localValue: {
      get() {
        return this.value;
      },
      set(newVal) {
        // 只在 newVal 是有效日期字符串时才触发更新
        if (newVal) {
          const date = new Date(newVal);
          if (!isNaN(date.getTime())) {
            this.$emit('input', newVal);
          }
        }
        else {
          this.$emit('input', newVal);
        }
      }
    },
    currentYear() {
      return this.currentDate.getFullYear();
    },
    currentMonth() {
      return this.currentDate.getMonth();
    },
    currentMonthDays() {
      const daysInMonth = new Date(this.currentYear, this.currentMonth + 1, 0).getDate();
      return Array.from({length: daysInMonth}, (_, i) => i + 1);
    },
    prevMonthDays() {
      const firstDayOfMonth = new Date(this.currentYear, this.currentMonth, 1).getDay();
      const daysInPrevMonth = new Date(this.currentYear, this.currentMonth, 0).getDate();
      return Array.from({length: firstDayOfMonth}, (_, i) => daysInPrevMonth - firstDayOfMonth + i + 1);
    },
    nextMonthDays() {
      const lastDayOfMonth = new Date(this.currentYear, this.currentMonth + 1, 0).getDay();
      const daysNeeded = 6 - lastDayOfMonth;
      return Array.from({length: daysNeeded}, (_, i) => i + 1);
    }
  },
  watch: {
    value: {
      handler(newVal) {
        if (newVal) {
          const date = new Date(newVal);
          // 检查日期是否有效
          if (!isNaN(date.getTime())) {
            this.selectedDate = date;
            this.selectedHour = date.getHours().toString().padStart(2, '0');
            this.selectedMinute = date.getMinutes().toString().padStart(2, '0');
            this.currentDate = new Date(date.getFullYear(), date.getMonth(), 1);
            // 更新组合输入框的值
            this.updateInputParts(date);
          }
        }
        else {
          // 清空组合输入框的值
          this.inputParts = {
            year: '',
            month: '',
            day: '',
            hour: '',
            minute: ''
          };
        }
      },
      immediate: true
    }
  },
  methods: {
    togglePicker() {
      if (this.showPicker) {
        this.showPicker = false;
      }
      else {
        this.openPicker();
      }
    },
    // 打开弹窗并初始化日历高亮与键盘焦点
    openPicker() {
      this.showPicker = true;
      if (this.value) {
        const date = new Date(this.value);
        this.currentDate = new Date(date.getFullYear(), date.getMonth(), 1);
        this.highlightedDate = date;
      }
      else {
        this.currentDate = new Date();
        // 没有当前日期时，以当天为日历高亮
        this.highlightedDate = new Date();
      }
      // 打开弹窗后计算显示位置
      this.updatePopupPosition();
      // 聚焦弹窗以接收键盘事件
      this.$nextTick(() => {
        if (this.$refs.pickerPopup) {
          this.$refs.pickerPopup.focus();
        }
      });
    },
    // 处理组件根元素的聚焦事件，与内部输入框的focus事件分开
    handleRootFocus() {
      if (!this.showPicker) {
        this.openPicker();
      }
    },
    // 处理弹窗的键盘事件，实现日历的键盘导航
    handlePopupKeydown(event) {
      const deltas = {ArrowLeft: -1, ArrowRight: 1, ArrowUp: -7, ArrowDown: 7};
      if (deltas[event.key] !== undefined) {
        event.preventDefault();
        this.updateHighlightedDate(deltas[event.key]);
        return;
      }
      if (event.key === 'Enter') {
        event.preventDefault();
        // 回车选择高亮的日期，并聚焦到时选择
        this.selectDate(this.highlightedDate.getDate());
        this.$nextTick(() => {
          if (this.$refs.popupHourSelect) {
            this.$refs.popupHourSelect.focus();
          }
        });
      }
    },
    // 更新日历高亮日期，跨月时自动翻页
    updateHighlightedDate(deltaDays) {
      if (!this.highlightedDate) {
        return;
      }
      const newDate = new Date(this.highlightedDate.getFullYear(), this.highlightedDate.getMonth(), this.highlightedDate.getDate() + deltaDays);
      this.highlightedDate = newDate;
      // 高亮日期不在当前展示月份时，翻页到该日期所在月份
      if (newDate.getFullYear() !== this.currentYear || newDate.getMonth() !== this.currentMonth) {
        this.currentDate = new Date(newDate.getFullYear(), newDate.getMonth(), 1);
      }
    },
    // 判断日历格子是否为键盘导航高亮日期
    isHighlighted(day) {
      if (!this.highlightedDate) {
        return false;
      }
      return this.highlightedDate.getDate() === day &&
        this.highlightedDate.getMonth() === this.currentMonth &&
        this.highlightedDate.getFullYear() === this.currentYear;
    },
    // 处理弹窗内时分下拉框的键盘事件
    handleTimeSelectKeydown(part, event) {
      // 阻止事件冒泡到弹窗，避免方向键同时移动日历高亮
      event.stopPropagation();
      if (event.key === 'Enter') {
        event.preventDefault();
        if (part === 'hour') {
          // 时选择回车后聚焦分选择
          this.$nextTick(() => {
            if (this.$refs.popupMinuteSelect) {
              this.$refs.popupMinuteSelect.focus();
            }
          });
        }
        else {
          // 分选择上回车等同于按确认按钮
          this.confirm();
        }
        return;
      }
      // 数字键输入，累积缓冲定位选项
      if (/^[0-9]$/.test(event.key)) {
        const bufferName = '_' + part + 'TypeBuffer';
        // 400ms内连续输入的数字作为整体处理
        clearTimeout(this[bufferName + 'Timer']);
        this[bufferName] = ((this[bufferName] || '') + event.key).slice(-2);
        this[bufferName + 'Timer'] = setTimeout(() => {
          this[bufferName] = '';
        }, 400);
        // 定位到匹配的选项
        const matched = this[bufferName].padStart(2, '0');
        const maxValue = part === 'hour' ? 24 : 60;
        if (parseInt(matched) < maxValue) {
          if (part === 'hour') {
            this.selectedHour = matched;
          }
          else {
            this.selectedMinute = matched;
          }
          this.updateDateTime();
        }
      }
    },
    // 根据屏幕剩余空间决定弹窗显示在输入框上方还是下方
    updatePopupPosition() {
      // 默认显示在下方
      this.popupPosition = 'bottom';
      this.$nextTick(() => {
        const popup = this.$el.querySelector('.picker-popup');
        if (!popup || !this.$el) {
          return;
        }
        const rect = this.$el.getBoundingClientRect();
        const popupHeight = popup.offsetHeight;
        const spaceBelow = window.innerHeight - rect.bottom;
        const spaceAbove = rect.top;
        // 下方空间不足且上方空间更充裕时，显示在上方
        if (spaceBelow < popupHeight + 8 && spaceAbove > spaceBelow) {
          this.popupPosition = 'top';
        }
      });
    },
    // 处理输入框的聚焦事件
    handleComboFocus(part) {
      if (!this.defaultToday) {
        return;
      }
      const {year, month, day, hour, minute} = this.inputParts;
      // 已有值时不做处理
      if (year || month || day || hour || minute) {
        return;
      }
      // 先将当前时间直接填入所有部分
      this.updateInputParts(new Date());
      this.updateCombinedValue();
      this.justDefaultedToday = true;
      // 再移动到日期部分（年份输入框）
      this.$nextTick(() => {
        if (this.$refs.yearInput) {
          this.$refs.yearInput.focus();
          this.selectInputText(this.$refs.yearInput);
        }
      });
    },
    // 处理输入框的点击事件
    handleInputClick(part, event) {
      // 刚通过defaultToday填入默认时间时，跳过本次点击处理，保持聚焦在日期部分
      if (this.justDefaultedToday) {
        this.justDefaultedToday = false;
        return;
      }
      const {year, month, day, hour, minute} = this.inputParts;

      // 检查是否所有输入框都没有值
      if (!year && !month && !day && !hour && !minute) {
        // 所有输入框都没有值，固定聚焦第一个输入框
        if (this.$refs.yearInput) {
          event.preventDefault();
          this.$refs.yearInput.focus();
          this.selectInputText(this.$refs.yearInput);
        }
      }
      else {
        // 有输入值，聚焦当前输入框并全选数值
        this.selectInputText(event.target);
      }
    },
    // 处理输入框容器的点击事件
    handleContainerClick() {
      const {year, month, day, hour, minute} = this.inputParts;

      // 检查是否所有输入框都没有值
      if (!year && !month && !day && !hour && !minute) {
        // 所有输入框都没有值，固定聚焦第一个输入框
        if (this.$refs.yearInput) {
          this.$refs.yearInput.focus();
          this.selectInputText(this.$refs.yearInput);
        }
      }
    },
    // 根据日期更新组合输入框的值
    updateInputParts(date) {
      this.inputParts = {
        year: date.getFullYear().toString(),
        month: (date.getMonth() + 1).toString().padStart(2, '0'),
        day: date.getDate().toString().padStart(2, '0'),
        hour: date.getHours().toString().padStart(2, '0'),
        minute: date.getMinutes().toString().padStart(2, '0')
      };
    },
    // 处理组合输入框的输入事件
    handleComboInput(part, event) {
      const value = event.target.value;
      // 只允许输入数字
      const numericValue = value.replace(/\D/g, '');

      // 确保值是字符串类型
      this.$set(this.inputParts, part, numericValue);

      // 日期部分输入分隔符（. 。 / -）时，预测补全当前部分并跳到下一部分
      if (/[.。/-]/.test(value) && (part === 'year' || part === 'month' || part === 'day')) {
        // 当前部分为空时不做处理，可能是上一部分完成后自动跳转带来的
        if (!numericValue) {
          event.target.value = '';
          return;
        }
        this.predictDatePart(part, event);
        return;
      }

      // 当输入达到最大长度时，进行验证并聚焦到下一个输入框
      if (numericValue.length === event.target.maxLength) {
        // 验证输入值是否有效
        if (this.isValidInput(part, numericValue)) {
          // 验证输入值是否在范围内
          if (this.isInRange(part, numericValue)) {
            this.focusNextInput(part);

            // 只有当所有输入框都达到最大长度时，才更新完整的日期时间值
            const {year, month, day, hour, minute} = this.inputParts;
            if (year.length === 4 && month.length === 2 && day.length === 2 && hour.length === 2 && minute.length ===
                2) {
              this.updateCombinedValue();
            }
          }
          else {
            // 输入值超出范围，清空当前输入框并重新聚焦
            this.$set(this.inputParts, part, '');
            event.target.focus();
            this.selectInputText(event.target);
          }
        }
        else {
          // 输入值无效，清空当前输入框并重新聚焦
          this.$set(this.inputParts, part, '');
          event.target.focus();
          this.selectInputText(event.target);
        }
      }
    },
    // 根据分隔符预测补全当前日期部分的值并跳到下一部分
    predictDatePart(part, event) {
      const predicted = this.predictValue(part);
      // 验证补全后的值是否有效且在范围内
      if (this.isValidInput(part, predicted) && this.isInRange(part, predicted)) {
        this.$set(this.inputParts, part, predicted);
        this.focusNextInput(part);
        // 只有当所有输入框都达到最大长度时，才更新完整的日期时间值
        const parts = this.inputParts;
        if (parts.year.length === 4 && parts.month.length === 2 && parts.day.length === 2 && parts.hour.length === 2 && parts.minute.length ===
            2) {
          this.updateCombinedValue();
        }
      }
      else {
        // 补全后的值无效，清空当前输入框并重新聚焦
        this.$set(this.inputParts, part, '');
        event.target.focus();
        this.selectInputText(event.target);
      }
    },
    // 预测补全指定部分的值
    predictValue(part) {
      const rawValue = this.inputParts[part];
      if (part === 'year') {
        if (rawValue.length === 4) {
          return rawValue;
        }
        // 取后两位数字，90及以上补全为199x，以下补全为20xx
        const twoDigits = rawValue.padStart(2, '0').slice(-2);
        if (parseInt(twoDigits) >= 90) {
          return '19' + twoDigits;
        }
        else {
          return '20' + twoDigits;
        }
      }
      // 月份日期时间部分输入单个数字时前面补全0
      return rawValue.padStart(2, '0');
    },
    // 验证输入值是否有效
    isValidInput(part, value) {
      const numValue = parseInt(value);

      switch (part) {
        case 'year':
          // 年份必须是有效的数字
          return !isNaN(numValue);
        case 'month':
          // 月份必须在 1-12 之间
          return !isNaN(numValue) && numValue >= 1 && numValue <= 12;
        case 'day':
          // 日期必须在 1-31 之间
          return !isNaN(numValue) && numValue >= 1 && numValue <= 31;
        case 'hour':
          // 小时必须在 0-23 之间（24小时制）
          return !isNaN(numValue) && numValue >= 0 && numValue <= 23;
        case 'minute':
          // 分钟必须在 0-59 之间
          return !isNaN(numValue) && numValue >= 0 && numValue <= 59;
        default:
          return true;
      }
    },
    // 验证输入值是否在范围内
    isInRange(part, value) {
      const numValue = parseInt(value);
      const {year, month, day, hour, minute} = this.inputParts;

      // 构建当前输入的日期时间对象
      let currentDate = null;
      switch (part) {
        case 'year':
          currentDate = new Date(numValue, 0, 1, 0, 0);
          break;
        case 'month':
          if (year) {
            currentDate = new Date(parseInt(year), numValue - 1, 1, 0, 0);
          }
          break;
        case 'day':
          if (year && month) {
            currentDate = new Date(parseInt(year), parseInt(month) - 1, numValue, 0, 0);
          }
          break;
        case 'hour':
          if (year && month && day) {
            currentDate = new Date(parseInt(year), parseInt(month) - 1, parseInt(day), numValue, 0);
          }
          break;
        case 'minute':
          if (year && month && day && hour) {
            currentDate = new Date(parseInt(year), parseInt(month) - 1, parseInt(day), parseInt(hour), numValue);
          }
          break;
        default:
          return true;
      }

      // 检查是否在范围内
      if (currentDate) {
        if (this.max) {
          const maxDate = new Date(this.max);
          // 对于日期部分的验证，只比较日期部分
          if (part === 'year' || part === 'month' || part === 'day') {
            const currentDateOnly = new Date(currentDate.getFullYear(), currentDate.getMonth(), currentDate.getDate());
            const maxDateOnly = new Date(maxDate.getFullYear(), maxDate.getMonth(), maxDate.getDate());
            if (currentDateOnly > maxDateOnly) {
              return false;
            }
          }
          else {
            // 对于时间部分的验证，比较完整的日期时间
            if (currentDate > maxDate) {
              return false;
            }
          }
        }
        if (this.min) {
          const minDate = new Date(this.min);
          // 对于日期部分的验证，只比较日期部分
          if (part === 'year' || part === 'month' || part === 'day') {
            const currentDateOnly = new Date(currentDate.getFullYear(), currentDate.getMonth(), currentDate.getDate());
            const minDateOnly = new Date(minDate.getFullYear(), minDate.getMonth(), minDate.getDate());
            if (currentDateOnly < minDateOnly) {
              return false;
            }
          }
          else {
            // 对于时间部分的验证，比较完整的日期时间
            if (currentDate < minDate) {
              return false;
            }
          }
        }
      }

      return true;
    },
    // 处理组合输入框的键盘事件
    handleComboKeyup(part, event) {
      // 处理退格键
      if (event.key === 'Backspace' && event.target.value === '') {
        this.focusPreviousInput(part);
      }
      // 处理方向键
      if (event.key === 'ArrowRight') {
        this.focusNextInput(part);
      }
      if (event.key === 'ArrowLeft') {
        this.focusPreviousInput(part);
      }
      // 处理回车键，跳转到下一个输入框并保留原值
      if (event.key === 'Enter') {
        const currentValue = this.inputParts[part];
        // 有值且未满长度时，先预测补全当前部分
        if (currentValue && currentValue.length < event.target.maxLength) {
          const predicted = this.predictValue(part);
          // 补全后的值有效且在范围内才更新
          if (this.isValidInput(part, predicted) && this.isInRange(part, predicted)) {
            this.$set(this.inputParts, part, predicted);
            // 只有当所有输入框都达到最大长度时，才更新完整的日期时间值
            const parts = this.inputParts;
            if (parts.year.length === 4 && parts.month.length === 2 && parts.day.length === 2 && parts.hour.length === 2 && parts.minute.length ===
                2) {
              this.updateCombinedValue();
            }
          }
        }
        const partsOrder = ['year', 'month', 'day', 'hour', 'minute'];
        const currentIndex = partsOrder.indexOf(part);
        if (currentIndex < partsOrder.length - 1) {
          const nextInput = this.$refs[partsOrder[currentIndex + 1] + 'Input'];
          if (nextInput) {
            nextInput.focus();
          }
        }
      }
    },
    // 聚焦到下一个输入框
    focusNextInput(currentPart) {
      const partsOrder = ['year', 'month', 'day', 'hour', 'minute'];
      const currentIndex = partsOrder.indexOf(currentPart);
      if (currentIndex < partsOrder.length - 1) {
        const nextPart = partsOrder[currentIndex + 1];
        const nextInput = this.$refs[nextPart + 'Input'];
        if (nextInput) {
          nextInput.focus();
          this.selectInputText(nextInput);
        }
      }
    },
    // 聚焦到上一个输入框
    focusPreviousInput(currentPart) {
      const partsOrder = ['year', 'month', 'day', 'hour', 'minute'];
      const currentIndex = partsOrder.indexOf(currentPart);
      if (currentIndex > 0) {
        const previousPart = partsOrder[currentIndex - 1];
        const previousInput = this.$refs[previousPart + 'Input'];
        if (previousInput) {
          previousInput.focus();
          this.selectInputText(previousInput);
        }
      }
    },
    // 选中输入框的文本
    selectInputText(inputElement) {
      inputElement.select();
    },
    // 更新完整的日期时间值
    updateCombinedValue() {
      const {year, month, day, hour, minute} = this.inputParts;
      if (year.length === 4 && month.length === 2 && day.length === 2 && hour.length === 2 && minute.length === 2) {
        // 确保所有值都是有效的数字
        const yearNum = parseInt(year);
        const monthNum = parseInt(month);
        const dayNum = parseInt(day);
        const hourNum = parseInt(hour);
        const minuteNum = parseInt(minute);

        // 检查值是否有效
        if (!isNaN(yearNum) && !isNaN(monthNum) && !isNaN(dayNum) && !isNaN(hourNum) && !isNaN(minuteNum)) {
          // 检查日期是否有效
          const date = new Date(yearNum, monthNum - 1, dayNum, hourNum, minuteNum);
          if (!isNaN(date.getTime())) {
            // 检查日期是否有效（避免无效日期如 2 月 31 日）
            if (date.getFullYear() === yearNum && date.getMonth() === monthNum - 1 && date.getDate() === dayNum) {
              // 格式化日期为标准格式
              const formattedYear = yearNum.toString();
              const formattedMonth = monthNum.toString().padStart(2, '0');
              const formattedDay = dayNum.toString().padStart(2, '0');
              const formattedHour = hourNum.toString().padStart(2, '0');
              const formattedMinute = minuteNum.toString().padStart(2, '0');
              const dateStr = `${formattedYear}-${formattedMonth}-${formattedDay}T${formattedHour}:${formattedMinute}`;

              // 检查是否超出范围
              let validDateStr = dateStr;
              if (this.max) {
                const maxDate = new Date(this.max);
                if (date > maxDate) {
                  validDateStr = this.max;
                }
              }
              if (this.min) {
                const minDate = new Date(this.min);
                if (date < minDate) {
                  validDateStr = this.min;
                }
              }

              // 确保 inputParts 对象保持字符串类型
              this.inputParts = {
                year: formattedYear,
                month: formattedMonth,
                day: formattedDay,
                hour: formattedHour,
                minute: formattedMinute
              };

              // 只有当 validDateStr 是有效日期字符串时才更新 localValue
              const validDate = new Date(validDateStr);
              if (!isNaN(validDate.getTime())) {
                this.localValue = validDateStr;
              }
            }
          }
        }
      }
    },
    // 处理原生日历输入框的输入事件
    handleNativeInput(event) {
      this.localValue = event.target.value;
      if (event.target.value) {
        const date = new Date(event.target.value);
        this.selectedDate = date;
        this.selectedHour = date.getHours().toString().padStart(2, '0');
        this.selectedMinute = date.getMinutes().toString().padStart(2, '0');
        this.updateInputParts(date);
      }
    },
    closePicker() {
      this.showPicker = false;
    },
    prevMonth() {
      this.currentDate = new Date(this.currentYear, this.currentMonth - 1, 1);
    },
    nextMonth() {
      this.currentDate = new Date(this.currentYear, this.currentMonth + 1, 1);
    },
    isSelected(day) {
      if (!this.selectedDate) return false;
      return this.selectedDate.getDate() === day &&
          this.selectedDate.getMonth() === this.currentMonth &&
          this.selectedDate.getFullYear() === this.currentYear;
    },
    isToday(day) {
      const today = new Date();
      return today.getDate() === day &&
          today.getMonth() === this.currentMonth &&
          today.getFullYear() === this.currentYear;
    },
    isDisabled(day) {
      const date = new Date(this.currentYear, this.currentMonth, day);

      // 只比较日期部分，不比较时间部分
      if (this.min) {
        const minDate = new Date(this.min);
        const dateOnly = new Date(date.getFullYear(), date.getMonth(), date.getDate());
        const minDateOnly = new Date(minDate.getFullYear(), minDate.getMonth(), minDate.getDate());
        if (dateOnly < minDateOnly) {
          return true;
        }
      }

      if (this.max) {
        const maxDate = new Date(this.max);
        const dateOnly = new Date(date.getFullYear(), date.getMonth(), date.getDate());
        const maxDateOnly = new Date(maxDate.getFullYear(), maxDate.getMonth(), maxDate.getDate());
        if (dateOnly > maxDateOnly) {
          return true;
        }
      }

      return false;
    },
    selectDate(day) {
      if (this.isDisabled(day)) return;
      this.selectedDate = new Date(this.currentYear, this.currentMonth, day);
      this.updateDateTime();
    },
    updateDateTime() {
      if (!this.selectedDate) return;

      const date = new Date(this.selectedDate);
      date.setHours(parseInt(this.selectedHour));
      date.setMinutes(parseInt(this.selectedMinute));

      // 检查是否在范围内
      if (this.min) {
        const minDate = new Date(this.min);
        if (date < minDate) {
          return;
        }
      }

      if (this.max) {
        const maxDate = new Date(this.max);
        if (date > maxDate) {
          return;
        }
      }

      this.localValue = this.formatDateTime(date);
    },
    isHourDisabled(hour) {
      if (!this.selectedDate) return false;

      const selectedDateStr = this.formatDate(this.selectedDate);
      const hourStr = hour.toString().padStart(2, '0');

      // 检查是否与 max 日期相同
      if (this.max) {
        const maxDate = new Date(this.max);
        const maxDateStr = this.formatDate(maxDate);
        if (selectedDateStr === maxDateStr) {
          const maxHour = maxDate.getHours();
          return hour > maxHour;
        }
      }

      // 检查是否与 min 日期相同
      if (this.min) {
        const minDate = new Date(this.min);
        const minDateStr = this.formatDate(minDate);
        if (selectedDateStr === minDateStr) {
          const minHour = minDate.getHours();
          return hour < minHour;
        }
      }

      return false;
    },
    isMinuteDisabled(minute) {
      if (!this.selectedDate) return false;

      const selectedDateStr = this.formatDate(this.selectedDate);
      const hourStr = this.selectedHour;
      const minuteStr = minute.toString().padStart(2, '0');

      // 检查是否与 max 日期和小时相同
      if (this.max) {
        const maxDate = new Date(this.max);
        const maxDateStr = this.formatDate(maxDate);
        const maxHour = maxDate.getHours().toString().padStart(2, '0');
        if (selectedDateStr === maxDateStr && hourStr === maxHour) {
          const maxMinute = maxDate.getMinutes();
          return minute > maxMinute;
        }
      }

      // 检查是否与 min 日期和小时相同
      if (this.min) {
        const minDate = new Date(this.min);
        const minDateStr = this.formatDate(minDate);
        const minHour = minDate.getHours().toString().padStart(2, '0');
        if (selectedDateStr === minDateStr && hourStr === minHour) {
          const minMinute = minDate.getMinutes();
          return minute < minMinute;
        }
      }

      return false;
    },
    formatDateTime(date) {
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      const hours = date.getHours().toString().padStart(2, '0');
      const minutes = date.getMinutes().toString().padStart(2, '0');
      return `${year}-${month}-${day}T${hours}:${minutes}`;
    },
    formatDate(date) {
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      return `${year}-${month}-${day}`;
    },
    handleInput(e) {
      // 打印输入事件
      console.log('输入事件:', e);
      console.log('输入值:', e.target.value);
      console.log('值:', this.localValue);

      let inputValue = e.target.value;
      if (inputValue) {
        // 尝试解析输入值
        let inputDate = null;
        try {
          inputDate = new Date(inputValue);
        }
        catch (error) {
          console.log('日期解析错误:', error);
        }

        // 只有当输入值可以解析为有效日期时，才进行范围检查
        if (inputDate && !isNaN(inputDate.getTime())) {
          // 检查是否超出 max 范围
          if (this.max) {
            const maxDate = new Date(this.max);
            if (inputDate > maxDate) {
              inputValue = this.max;
            }
          }

          // 检查是否超出 min 范围
          if (this.min) {
            const minDate = new Date(this.min);
            if (inputDate < minDate) {
              inputValue = this.min;
            }
          }

          // 更新值
          this.localValue = inputValue;
          if (inputValue) {
            const date = new Date(inputValue);
            this.selectedDate = date;
            this.selectedHour = date.getHours().toString().padStart(2, '0');
            this.selectedMinute = date.getMinutes().toString().padStart(2, '0');
          }
        }
      }
      else {
        this.localValue = '';
        this.selectedDate = null;
        this.selectedHour = '00';
        this.selectedMinute = '00';
      }
    },
    confirm() {
      // 检查选择的日期时间是否在有效范围内
      if (this.isDateTimeValid()) {
        this.errorMessage = '';
        this.closePicker();
      }
      else {
        this.errorMessage = '日期时间选择超出范围';
      }
    },
    // 检查选择的日期时间是否有效
    isDateTimeValid() {
      if (!this.selectedDate) return false;

      const date = new Date(this.selectedDate);
      date.setHours(parseInt(this.selectedHour));
      date.setMinutes(parseInt(this.selectedMinute));

      // 检查是否在范围内
      if (this.min) {
        const minDate = new Date(this.min);
        if (date < minDate) {
          return false;
        }
      }

      if (this.max) {
        const maxDate = new Date(this.max);
        if (date > maxDate) {
          return false;
        }
      }

      return true;
    },
    reset() {
      this.localValue = '';
      this.selectedDate = null;
      this.selectedHour = '00';
      this.selectedMinute = '00';
      this.errorMessage = '';
      this.closePicker();
    }
  }
};
</script>

<style scoped>
.date-time-picker {
  position: relative;
  display: inline-block;
  width: fit-content;
}

.input-container {
  position: relative;
  display: inline-block;
  width: fit-content;
}

.datetime-input {
  padding: 5px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
  width: 100%;
  cursor: pointer;
  /* 恢复原生日历图标 */
  -webkit-appearance: auto;
  -moz-appearance: auto;
  appearance: auto;
  box-sizing: border-box;
}

/* 组合输入框样式 */
.combo-input {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  padding: 5px 35px 5px 5px;
  background-color: white;
  box-sizing: border-box;
  overflow: hidden;
  /* 行高重置为默认，避免继承 el-form-item 的大行高导致输入框过高 */
  line-height: normal;
}

.combo-input-part {
  border: none;
  outline: none;
  font-size: 14px;
  text-align: center;
  background: transparent;
  /* 使用等宽字体确保每个字符宽度一致 */
  font-family: 'Courier New', monospace;
  /* 设置基于字符数量的固定宽度 */
  flex-shrink: 0;
}

.combo-input-year {
  width: 56px; /* 4个字符的宽度 */
}

.combo-input-month,
.combo-input-day,
.combo-input-hour,
.combo-input-minute {
  width: 28px; /* 2个字符的宽度 */
}

.combo-input-separator {
  margin: 0 2px;
  color: #666;
  user-select: none;
  /* 使用等宽字体确保对齐 */
  font-family: 'Courier New', monospace;
}

/* 隐藏原生日历输入框，但保留其图标 */
.combo-input-native {
  position: absolute;
  top: 0;
  right: 0;
  width: 30px;
  height: 100%;
  opacity: 0;
  cursor: pointer;
  z-index: 1;
}

/* 为每个输入框添加占位符效果 */
.combo-input-year::placeholder {
  content: '----';
  color: #999;
}

.combo-input-month::placeholder,
.combo-input-day::placeholder,
.combo-input-hour::placeholder,
.combo-input-minute::placeholder {
  content: '--';
  color: #999;
}

/* 恢复原生日历图标显示 */
.datetime-input::-webkit-calendar-picker-indicator {
  display: block;
  -webkit-appearance: auto;
  cursor: pointer;
}

.datetime-input::-moz-calendar-picker-indicator {
  display: block;
  -moz-appearance: auto;
  cursor: pointer;
}

.datetime-input::-ms-clear {
  display: block;
}

.datetime-input::-ms-reveal {
  display: block;
}

.input-mask {
  position: absolute;
  top: 0;
  right: 0;
  width: 30px;
  height: 100%;
  border: 1px solid transparent;
  cursor: pointer;
  z-index: 1;
  /* 透明 */
  background-color: transparent;
  box-sizing: content-box;
  /* 自定义日历图标 */
  display: flex;
  align-items: center;
  justify-content: center;
}

.input-mask::before {
  content: '';
  width: 16px;
  height: 16px;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='%23666' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Crect x='3' y='4' width='18' height='18' rx='2' ry='2'%3E%3C/rect%3E%3Cline x1='16' y1='2' x2='16' y2='6'%3E%3C/line%3E%3Cline x1='8' y1='2' x2='8' y2='6'%3E%3C/line%3E%3Cline x1='3' y1='10' x2='21' y2='10'%3E%3C/line%3E%3C/svg%3E");
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
}

.picker-popup {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 5px;
  background: white;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  width: 300px;
  /* 明确指定行高，避免继承el-form-item的大行高导致内部元素过高 */
  line-height: 1.4;
}

/* 弹窗显示在输入框上方时的定位 */
.picker-popup-top {
  top: auto;
  bottom: 100%;
  margin-top: 0;
  margin-bottom: 5px;
}

.picker-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 6px 2px 6px;
  border-bottom: 1px solid #f0f0f0;
}

.picker-header button {
  background: none;
  border: none;
  font-size: 15px;
  cursor: pointer;
  padding: 2px;
}

.picker-header h3 {
  margin: 0;
  font-size: 13px;
}

.weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  padding: 3px 0;
  border-bottom: 1px solid #f0f0f0;
}

.weekday {
  text-align: center;
  font-size: 11px;
  color: #999;
  padding: 1px;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 1px;
  padding: 4px;
}

.calendar-day {
  text-align: center;
  padding: 4px;
  cursor: pointer;
  border-radius: 4px;
  font-size: 12px;
}

.calendar-day:hover:not(.disabled):not(.other-month) {
  background-color: #f5f5f5;
}

.calendar-day.selected {
  background-color: #1890ff;
  color: white;
}

.calendar-day.today {
  border: 1px solid #1890ff;
}

/* 键盘导航的高亮日期 */
.calendar-day.highlighted {
  border: 1px solid #1890ff;
  background-color: #e6f7ff;
}

.calendar-day.other-month {
  color: #ccc;
}

.calendar-day.disabled {
  color: #ccc;
  cursor: not-allowed;
}

.time-selector {
  display: flex;
  justify-content: center;
  padding: 5px;
  border-top: 1px solid #f0f0f0;
}

.time-input {
  margin: 0 10px;
  display: flex;
  align-items: center;
}

.time-input label {
  margin-right: 5px;
  font-size: 14px;
}

.time-input select {
  padding: 5px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  background-color: white;
  /* 确保在火狐浏览器中显示为白色 */
  -moz-appearance: none;
  appearance: none;
  color: #333;
}

.time-input select option:disabled {
  color: #ccc;
  cursor: not-allowed;
}


.picker-footer {
  display: flex;
  justify-content: flex-end;
  padding: 6px;
  gap: 10px;
  border-top: 1px solid #f0f0f0;
}

.picker-footer button {
  padding: 3px 12px;
  margin-left: 10px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  background: white;
  cursor: pointer;
}

.picker-footer button:last-child {
  background-color: #1890ff;
  color: white;
  border-color: #1890ff;
}

.backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
}
</style>