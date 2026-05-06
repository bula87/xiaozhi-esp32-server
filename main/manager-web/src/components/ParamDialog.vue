<template>
  <el-dialog
    :title="title"
    :visible.sync="visible"
    width="520px"
    class="param-dialog-wrapper"
    :append-to-body="true"
    :close-on-click-modal="false"
    :key="dialogKey"
    custom-class="custom-param-dialog"
    :show-close="false"
  >
    <div class="dialog-container">
      <div class="dialog-header">
        <h2 class="dialog-title">{{ title }}</h2>
        <button class="custom-close-btn" @click="cancel">
          <svg
            width="14"
            height="14"
            viewBox="0 0 14 14"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M13 1L1 13M1 1L13 13"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
            />
          </svg>
        </button>
      </div>

      <el-form
        :model="form"
        :rules="rules"
        ref="form"
        label-width="auto"
        label-position="left"
        class="param-form"
      >
        <el-form-item
          :label="$t('paramDialog.paramCode')"
          prop="paramCode"
          class="form-item"
        >
          <el-input
            v-model="form.paramCode"
            :placeholder="$t('paramDialog.paramCodePlaceholder')"
            class="custom-input"
          ></el-input>
        </el-form-item>

        <el-form-item
          :label="$t('paramDialog.valueType')"
          prop="valueType"
          class="form-item"
        >
          <el-select
            v-model="form.valueType"
            :placeholder="$t('paramDialog.valueTypePlaceholder')"
            class="custom-select"
          >
            <el-option
              v-for="item in valueTypeOptions"
              :key="item.value"
              :label="$t(`paramDialog.${item.value}Type`)"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item
          :label="$t('paramDialog.paramValue')"
          prop="paramValue"
          class="form-item"
        >
          <el-input
            v-if="form.valueType !== 'json' && form.valueType !== 'array'"
            v-model="form.paramValue"
            :placeholder="$t('paramDialog.paramValuePlaceholder')"
            class="custom-input"
          ></el-input>
          <el-input
            v-else
            type="textarea"
            v-model="form.paramValue"
            :placeholder="$t('paramDialog.paramValuePlaceholder')"
            :rows="6"
            class="custom-textarea"
          ></el-input>
        </el-form-item>

        <el-form-item
          :label="$t('paramDialog.remark')"
          prop="remark"
          class="form-item remark-item"
        >
          <el-input
            type="textarea"
            v-model="form.remark"
            :placeholder="$t('paramDialog.remarkPlaceholder')"
            :rows="3"
            class="custom-textarea"
          ></el-input>
        </el-form-item>
      </el-form>

      <div class="dialog-footer">
        <el-button
          type="primary"
          @click="submit"
          class="save-btn"
          :loading="saving"
          :disabled="saving"
        >
          {{ $t("paramDialog.save") }}
        </el-button>
        <el-button @click="cancel" class="cancel-btn">
          {{ $t("paramDialog.cancel") }}
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'ParamDialog',
  props: {
    title: {
      type: String,
      default: "Add Parameter",
    },
    visible: {
      type: Boolean,
      default: false,
    },
    form: {
      type: Object,
      default: () => ({
        id: null,
        paramCode: "",
        paramValue: "",
        valueType: "string",
        remark: "",
      }),
    },
  },
  data() {
    return {
      dialogKey: Date.now(),
      saving: false,
      valueTypeOptions: [
        { value: "string" },
        { value: "number" },
        { value: "boolean" },
        { value: "array" },
        { value: "json" },
      ],
      rules: {
        paramCode: [
          {
            required: true,
            message: this.$t("paramDialog.requiredParamCode"),
            trigger: "blur",
          },
        ],
        paramValue: [
          {
            required: true,
            message: this.$t("paramDialog.requiredParamValue"),
            trigger: "blur",
          },
        ],
        valueType: [
          {
            required: true,
            message: this.$t("paramDialog.requiredValueType"),
            trigger: "change",
          },
        ],
      },
    };
  },
  methods: {
    submit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          const submitData = { ...this.form };

          // If type is array, validate format and convert
          if (submitData.valueType === "array" && submitData.paramValue) {
            const lines = submitData.paramValue
              .split("\n")
              .filter((line) => line.trim());

            // Check if each line except the last ends with a semicolon
            for (let i = 0; i < lines.length - 1; i++) {
              if (!lines[i].trim().endsWith(";")) {
                this.$message.error(
                  "Array format error, needs to use English semicolon ending",
                );
                return;
              }
            }

            const items = lines
              .map((item) => item.trim().replace(/;$/, ""))
              .filter((item) => item);
            submitData.paramValue = items.join(";");
          }
          // If type is json, compress JSON format before submitting
          else if (submitData.valueType === "json" && submitData.paramValue) {
            try {
              const parsed = JSON.parse(submitData.paramValue);
              submitData.paramValue = JSON.stringify(parsed);
            } catch (e) {
              // If parsing fails, keep the original value
            }
          }

          this.saving = true; // Start loading
          this.$emit("submit", submitData);
        }
      });
    },
    cancel() {
      this.saving = false; // Reset state on cancel
      this.dialogKey = Date.now();
      this.$emit("cancel");
    },

    // Provided for parent component to reset saving state
    resetSaving() {
      this.saving = false;
    },
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        if (this.form.paramValue) {
          // If type is json, format for display
          if (this.form.valueType === "json") {
            try {
              const parsed = JSON.parse(this.form.paramValue);
              this.form.paramValue = JSON.stringify(parsed, null, 2);
            } catch (e) {
              // If parsing fails, keep the original value
            }
          }
          // If type is array, convert semicolon-separated string to one item per line
          else if (this.form.valueType === "array") {
            const items = this.form.paramValue
              .split(";")
              .filter((item) => item.trim());
            this.form.paramValue = items.join(";\n");
          }
        }
      } else {
        // Reset saving state when dialog closes
        this.saving = false;
      }
    },
  },
};
</script>

<style>
.custom-param-dialog {
  border-radius: 16px !important;
  overflow: hidden;
  box-shadow: var(--ui-shadow-lg) !important;
  border: 1px solid var(--ui-border) !important;
  background: var(--ui-bg-panel) !important;

  .el-dialog__header {
    display: none;
  }

  .el-dialog__body {
    padding: 0 !important;
    border-radius: 16px;
    background: var(--ui-bg-panel) !important;
  }
}
</style>

<style scoped lang="scss">
@import "../styles/aurora-theme.scss";

.param-dialog-wrapper {
  .dialog-container {
    padding: 24px 32px;
    background: linear-gradient(
      145deg,
      rgba(19, 24, 36, 0.98) 0%,
      rgba(15, 23, 42, 0.99) 50%,
      rgba(19, 24, 36, 0.98) 100%
    );
    border: 1px solid $aurora-border;
    border-radius: 14px;
  }

  .dialog-header {
    position: relative;
    margin-bottom: 24px;
    text-align: center;
  }

  .dialog-title {
    font-size: 20px;
    color: $aurora-text-primary;
    margin: 0;
    padding: 0;
    font-weight: 600;
    letter-spacing: 0.5px;
    font-family: $aurora-font-mono;
  }

  .custom-close-btn {
    position: absolute;
    top: -8px;
    right: -8px;
    width: 32px;
    height: 32px;
    border-radius: 50%;
    border: 1px solid $aurora-border;
    background: $bg-panel-hover;
    color: $aurora-text-secondary;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0;
    outline: none;
    transition: all 0.2s ease;

    &:hover {
      color: #fff;
      background: rgba(239, 68, 68, 0.35);
      border-color: rgba(248, 113, 113, 0.5);
      transform: rotate(90deg);
    }
  }

  .param-form {
    .form-item {
      margin-bottom: 20px;

      :deep(.el-form-item__label) {
        color: $aurora-text-secondary;
        font-weight: 500;
        padding-right: 12px;
        text-align: right;
        font-size: 14px;
        letter-spacing: 0.2px;
      }
    }

    .custom-input {
      :deep(.el-input__inner) {
        background-color: $bg-panel;
        border-radius: 8px;
        border: 1px solid $border-color;
        height: 42px;
        padding: 0 14px;
        font-size: 14px;
        color: $aurora-text-primary;
        font-family: $aurora-font-mono;

        &:focus {
          border-color: var(--ui-focus-border);
          box-shadow: var(--ui-focus-shadow);
        }

        &::placeholder {
          color: $text-muted;
        }
      }
    }

    .custom-select {
      width: 100%;

      :deep(.el-input__inner) {
        background-color: $bg-panel;
        border-radius: 8px;
        border: 1px solid $border-color;
        height: 42px;
        font-size: 14px;
        color: $aurora-text-primary;
        font-family: $aurora-font-mono;

        &:focus {
          border-color: var(--ui-focus-border);
          box-shadow: var(--ui-focus-shadow);
        }

        &::placeholder {
          color: $text-muted;
        }
      }
    }

    .custom-textarea {
      :deep(.el-textarea__inner) {
        background-color: $bg-panel;
        border-radius: 8px;
        border: 1px solid $border-color;
        padding: 12px 14px;
        font-size: 14px;
        color: $aurora-text-primary;
        font-family: $aurora-font-mono;
        line-height: 1.5;

        &:focus {
          border-color: var(--ui-focus-border);
          box-shadow: var(--ui-focus-shadow);
        }

        &::placeholder {
          color: $text-muted;
        }
      }
    }

    .remark-item :deep(.el-form-item__label) {
      margin-top: -4px;
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: center;
    padding: 16px 0 0;
    margin-top: 16px;

    .save-btn {
      width: 120px;
      height: 42px;
      font-size: 14px;
      font-weight: 600;
      border-radius: 8px;
      font-family: $aurora-font-mono;
      background: var(--ui-btn-primary-bg) !important;
      color: var(--ui-btn-primary-text) !important;
      border: none !important;
      letter-spacing: 0.5px;

      &:hover,
      &:focus {
        filter: brightness(1.08);
      }
    }

    .cancel-btn {
      width: 120px;
      height: 42px;
      font-size: 14px;
      font-weight: 500;
      border-radius: 8px;
      background: $bg-panel-hover !important;
      color: $aurora-text-secondary !important;
      border: 1px solid $border-color !important;
      margin-left: 16px;
      letter-spacing: 0.5px;

      &:hover,
      &:focus {
        border-color: var(--ui-focus-border) !important;
        color: $accent-cyan !important;
      }
    }
  }
}
</style>
