<template>
  <div class="page-container">
    <div class="operation-bar">
      <h2 class="page-title">{{ $t("serverSideManager.pageTitle") }}</h2>
      <div class="right-operations">
        </div>
    </div>

    <div class="main-wrapper">
      <div class="content-panel">
        <div class="content-area">
          <el-card class="server-card" shadow="never">
            <el-table
              ref="paramsTable"
              :data="paramsList"
              class="transparent-table"
              v-loading="loading"
              height="100%"
              :element-loading-text="$t('common.loading')"
              element-loading-spinner="el-icon-loading"
              element-loading-background="rgba(11, 15, 25, 0.8)"
              :header-cell-class-name="headerCellClassName"
            >
              <el-table-column align="center" width="80">
                <template slot="header">
                  <span class="selection-header-text">{{ $t('modelConfig.select') }}</span>
                </template>
                <template slot-scope="scope">
                  <el-checkbox v-model="scope.row.selected"></el-checkbox>
                </template>
              </el-table-column>

              <el-table-column
                :label="$t('serverSideManager.wsAddress')"
                prop="address"
                align="center"
              >
                <template slot-scope="scope">
                  <span class="address-text">{{ scope.row.address }}</span>
                </template>
              </el-table-column>

              <el-table-column
                :label="$t('serverSideManager.operation')"
                align="center"
                width="280"
              >
                <template slot-scope="scope">
                  <div class="action-cell">
                    <el-button
                      size="mini"
                      type="text"
                      class="restart-btn"
                      @click="emitAction(scope.row, actionMap.restart)"
                    >
                      <i class="el-icon-refresh"></i> {{ $t("serverSideManager.restart") }}
                    </el-button>
                    <el-button
                      size="mini"
                      type="text"
                      class="update-btn"
                      @click="emitAction(scope.row, actionMap.update_config)"
                    >
                      <i class="el-icon-setting"></i> {{ $t("serverSideManager.updateConfig") }}
                    </el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>

            <div class="table_bottom">
              <div class="ctrl_btn">
                </div>

              <div class="custom-pagination">
                <span class="total-text">{{ $t("modelConfig.totalRecords", { total }) }}</span>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <div class="footer-container">
      <version-footer />
    </div>
  </div>
</template>

<script>
import Api from "@/apis/api";
import VersionFooter from "@/components/VersionFooter.vue";

export default {
  name: 'ServerSideManager',
  components: { VersionFooter },
  data() {
    return {
      paramsList: [],
      loading: false,
      total: 0,
    };
  },
  created() {
    this.fetchParams();
  },
  computed: {
    actionMap() {
      return {
        restart: {
          value: "restart",
          title: this.$t("serverSideManager.restartServer"),
          message: this.$t("serverSideManager.confirmRestart"),
          confirmText: this.$t("serverSideManager.restart"),
        },
        update_config: {
          value: "update_config",
          title: this.$t("serverSideManager.updateConfigTitle"),
          message: this.$t("serverSideManager.confirmUpdateConfig"),
          confirmText: this.$t("serverSideManager.updateConfig"),
        },
      };
    },
  },
  methods: {
    fetchParams() {
      this.loading = true;
      Api.admin.getWsServerList({}, ({ data }) => {
        this.loading = false;
        if (data.code === 0) {
          this.paramsList = data.data.map((item) => ({ address: item, selected: false }));
          this.total = data.data.length;
        } else {
          this.$message.error({
            message: data.msg || this.$t("serverSideManager.getServerListFailed"),
            showClose: true,
          });
        }
      });
    },
    emitAction(rowItem, actionItem) {
      if (!actionItem || !rowItem.address) return;

      this.$confirm(actionItem.message, actionItem.title, {
        confirmButtonText: actionItem.confirmText,
        cancelButtonText: this.$t("common.cancel"),
        type: 'warning'
      }).then(() => {
        Api.admin.sendWsServerAction(
          { targetWs: rowItem.address, action: actionItem.value },
          ({ data }) => {
            if (data.code !== 0) {
              this.$message.error({
                message: data.msg || this.$t("serverSideManager.operationFailed"),
                showClose: true,
              });
              return;
            }
            this.$message.success({
              message: actionItem.value === "restart"
                ? this.$t("serverSideManager.restartSuccess")
                : this.$t("serverSideManager.updateConfigSuccess"),
              showClose: true,
            });
          }
        );
      }).catch(() => {});
    },
    headerCellClassName({ columnIndex }) {
      return columnIndex === 0 ? "custom-selection-header" : "";
    },
  },
};
</script>
<style lang="scss" scoped>
@import "../styles/aurora-theme.scss";

/* --- Unique styles for ServerSideManager --- */
.server-name-text { color: $accent-cyan; font-weight: bold; }
</style>
