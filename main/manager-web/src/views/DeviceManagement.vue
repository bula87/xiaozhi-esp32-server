<template>
  <div class="page-container">
    <div class="operation-bar">
      <h2 class="page-title">{{ $t('device.management') }}</h2>
      <div class="right-operations">
        <el-input 
          :placeholder="$t('device.searchPlaceholder')" 
          v-model="searchKeyword" 
          class="search-input"
          @keyup.enter.native="handleSearch" 
          clearable 
        />
        <el-button class="btn-search" @click="handleSearch">
          {{ $t('device.search') }}
        </el-button>
      </div>
    </div>

    <div class="main-wrapper">
      <div class="content-panel">
        <div class="content-area">
          <el-card class="device-card" shadow="never">
            <el-table 
              ref="deviceTable" 
              :data="paginatedDeviceList" 
              class="transparent-table"
              :header-cell-class-name="headerCellClassName" 
              v-loading="loading"
              height="100%"
              :element-loading-text="$t('common.loading')" 
              element-loading-spinner="el-icon-loading"
              element-loading-background="rgba(11, 15, 25, 0.8)"
              :header-cell-class-name="headerCellClassName" >
            >
              <el-table-column align="center" width="80">
                <template slot="header">
                  <span class="selection-header-text">{{ $t('modelConfig.select') }}</span>
                </template>
                <template slot-scope="scope">
                  <el-checkbox v-model="scope.row.selected"></el-checkbox>
                </template>
              </el-table-column>

              <el-table-column :label="$t('device.model')" prop="model" align="center">
                <template slot-scope="scope">
                  <span class="model-text">{{ getFirmwareTypeName(scope.row.model) }}</span>
                </template>
              </el-table-column>

              <el-table-column :label="$t('device.firmwareVersion')" prop="firmwareVersion" align="center" width="120" />
              <el-table-column :label="$t('device.macAddress')" prop="macAddress" align="center" width="160" />

              <el-table-column :label="$t('device.bindTime')" prop="bindTime" align="center" width="170">
                <template slot-scope="scope">
                  <span class="time-text">{{ scope.row.bindTime }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('device.lastConversation')" prop="lastConversation" align="center" width="170">
                <template slot-scope="scope">
                  <span class="time-text">{{ scope.row.lastConversation }}</span>
                </template>
              </el-table-column>

              <el-table-column v-if="mqttServiceAvailable" :label="$t('device.deviceStatus')" prop="deviceStatus" align="center" width="100">
                <template slot-scope="scope">
                  <el-tag v-if="scope.row.deviceStatus === 'online'" type="success" size="mini" effect="dark" class="aurora-tag">
                    {{ $t('device.online') }}
                  </el-tag>
                  <el-tag v-else type="danger" size="mini" effect="dark" class="aurora-tag">
                    {{ $t('device.offline') }}
                  </el-tag>
                </template>
              </el-table-column>

              <el-table-column :label="$t('device.remark')" align="center" min-width="150">
                <template #default="{ row }">
                  <el-input 
                    v-show="row.isEdit" 
                    v-model="row.remark" 
                    size="mini" 
                    maxlength="64" 
                    show-word-limit
                    @blur="onRemarkBlur(row)" 
                    @keyup.enter.native="onRemarkEnter(row)" 
                  />
                  <span v-show="!row.isEdit" class="remark-view">
                    <i class="el-icon-edit" @click="row.isEdit = true" style="cursor: pointer; margin-right: 5px;"></i>
                    <span @click="row.isEdit = true" class="remark-text">
                      {{ row.remark || '-' }}
                    </span>
                  </span>
                </template>
              </el-table-column>

              <el-table-column :label="$t('device.autoUpdate')" align="center" width="100">
                <template slot-scope="scope">
                  <el-switch 
                    v-model="scope.row.otaSwitch" 
                    class="aurora-switch"
                    @change="handleOtaSwitchChange(scope.row)"
                  ></el-switch>
                </template>
              </el-table-column>

              <el-table-column :label="$t('device.operation')" align="center" width="200">
                <template slot-scope="scope">
                  <el-button v-if="isGenerate(scope.row)" size="mini" type="text" @click="handleGenertor(scope.row)">
                    {{ $t('device.deviceThemeGeneration') }}
                  </el-button>
                  <el-button size="mini" type="text" class="delete-btn" @click="handleUnbind(scope.row.device_id)">
                    {{ $t('device.unbind') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="table_bottom">
              <div class="ctrl_btn">
                <el-button size="mini" type="primary" @click="handleSelectAll">
                  {{ isCurrentPageAllSelected ? $t('common.deselectAll') : $t('common.selectAll') }}
                </el-button>
                <el-button type="success" size="mini" @click="handleAddDevice">
                  {{ $t('device.bindWithCode') }}
                </el-button>
                <el-button type="success" size="mini" @click="handleManualAddDevice">
                  {{ $t('device.manualAdd') }}
                </el-button>
                <el-button size="mini" type="danger" icon="el-icon-delete" @click="deleteSelected">
                  {{ $t('device.unbind') }}
                </el-button>
              </div>

              <div class="custom-pagination">
                <el-pagination
                  background
                  layout="total, sizes, prev, pager, next"
                  :current-page.sync="currentPage"
                  :page-size.sync="pageSize"
                  :total="filteredDeviceList.length"
                  :page-sizes="pageSizeOptions"
                  @size-change="handlePageSizeChange"
                  @current-change="goToPage"
                />
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <AddDeviceDialog 
      :visible.sync="addDeviceDialogVisible" 
      :agent-id="currentAgentId"
      @refresh="fetchBindDevices(currentAgentId)" 
    />
    <ManualAddDeviceDialog 
      :visible.sync="manualAddDeviceDialogVisible" 
      :agent-id="currentAgentId"
      @refresh="fetchBindDevices(currentAgentId)" 
    />
    
    <div class="footer-container">
      <version-footer />
    </div>
  </div>
</template>

<script>
import Api from '@/apis/api';
import AddDeviceDialog from "@/components/AddDeviceDialog.vue";
import ManualAddDeviceDialog from "@/components/ManualAddDeviceDialog.vue";
import VersionFooter from "@/components/VersionFooter.vue";

export default {
  name: 'DeviceManagement',
  components: {
    AddDeviceDialog,
    ManualAddDeviceDialog,
    VersionFooter
  },
  data() {
    return {
      addDeviceDialogVisible: false,
      manualAddDeviceDialogVisible: false,
      selectedDeviceId: '',
      searchKeyword: "",
      activeSearchKeyword: "",
      currentAgentId: this.$route.query.agentId || '',
      currentPage: 1,
      pageSize: 10,
      pageSizeOptions: [10, 20, 50, 100],
      deviceList: [],
      loading: false,
      firmwareTypes: [],
      mqttServiceAvailable: false,
    };
  },
  computed: {
    filteredDeviceList() {
      const keyword = this.activeSearchKeyword.toLowerCase();
      if (!keyword) return this.deviceList;
      return this.deviceList.filter(device =>
        (device.model && device.model.toLowerCase().includes(keyword)) ||
        (device.macAddress && device.macAddress.toLowerCase().includes(keyword))
      );
    },
    paginatedDeviceList() {
      const start = (this.currentPage - 1) * this.pageSize;
      return this.filteredDeviceList.slice(start, start + this.pageSize);
    },
    pageCount() {
      return Math.ceil(this.filteredDeviceList.length / this.pageSize);
    },
    isCurrentPageAllSelected() {
      return this.paginatedDeviceList.length > 0 &&
        this.paginatedDeviceList.every(device => device.selected);
    }
  },
  mounted() {
    const agentId = this.$route.query.agentId;
    if (agentId) this.fetchBindDevices(agentId);
  },
  created() {
    this.getFirmwareTypes();
  },
  methods: {
    async getFirmwareTypes() {
      try {
        const res = await Api.dict.getDictDataByType('FIRMWARE_TYPE');
        this.firmwareTypes = res.data;
      } catch (error) {
        this.$message.error(error.message || this.$t('device.getFirmwareTypeFailed'));
      }
    },
    handlePageSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1;
    },
    handleSearch() {
      this.activeSearchKeyword = this.searchKeyword;
      this.currentPage = 1;
    },
    handleSelectAll() {
      const shouldSelectAll = !this.isCurrentPageAllSelected;
      this.paginatedDeviceList.forEach(row => row.selected = shouldSelectAll);
    },
    deleteSelected() {
      const selectedDevices = this.paginatedDeviceList.filter(device => device.selected);
      if (selectedDevices.length === 0) return this.$message.warning(this.$t('device.selectAtLeastOne'));

      this.$confirm(this.$t('device.confirmBatchUnbind').replace('{count}', selectedDevices.length), this.$t('message.warning'), {
        type: 'warning'
      }).then(() => {
        this.batchUnbindDevices(selectedDevices.map(d => d.device_id));
      });
    },
    batchUnbindDevices(deviceIds) {
      const promises = deviceIds.map(id => new Promise((resolve, reject) => {
        Api.device.unbindDevice(id, ({ data }) => {
          if (data.code === 0) resolve();
          else reject(data.msg || this.$t('device.bindFailed'));
        });
      }));
      Promise.all(promises)
        .then(() => {
          this.$message.success(this.$t('device.batchUnbindSuccess').replace('{count}', deviceIds.length));
          this.fetchBindDevices(this.currentAgentId);
        })
        .catch(error => this.$message.error(error || this.$t('device.batchUnbindError')));
    },
    handleAddDevice() { this.addDeviceDialogVisible = true; },
    handleManualAddDevice() { this.manualAddDeviceDialogVisible = true; },
    submitRemark(row) {
      if (row._submitting) return;
      const text = (row.remark || '').trim();
      if (text.length > 64) return this.$message.warning(this.$t('device.remarkTooLong'));
      if (text === row._originalRemark) return;

      row._submitting = true;
      this.updateDeviceInfo(row.device_id, { alias: text }, (ok, resp) => {
        if (ok) {
          row._originalRemark = text;
          this.$message.success(this.$t('device.remarkSaved'));
        } else {
          row.remark = row._originalRemark;
          this.$message.error(resp.msg || this.$t('device.remarkSaveFailed'));
        }
        row._submitting = false;
      });
    },
    onRemarkBlur(row) {
      row.isEdit = false;
      setTimeout(() => this.submitRemark(row), 100);
    },
    onRemarkEnter(row) {
      row.isEdit = false;
      this.submitRemark(row);
    },
    handleUnbind(device_id) {
      this.$confirm(this.$t('device.confirmUnbind'), this.$t('message.warning'), { type: 'warning' }).then(() => {
        Api.device.unbindDevice(device_id, ({ data }) => {
          if (data.code === 0) {
            this.$message.success(this.$t('device.unbindSuccess'));
            this.fetchBindDevices(this.$route.query.agentId);
          } else {
            this.$message.error(data.msg || this.$t('device.unbindFailed'));
          }
        });
      });
    },
    handleGenertor(row) {
      const basePath = window.location.pathname.split('/').slice(0, -1).join('/');
      const url = `${window.location.origin}${basePath}/generator/?deviceId=${row.device_id}`;
      sessionStorage.setItem('devicePath', window.location.href);
      window.location.href = url;
    },
    goToPage(page) {
      this.currentPage = page;
    },
    fetchBindDevices(agentId) {
      this.loading = true;
      Api.device.getAgentBindDevices(agentId, ({ data }) => {
        this.loading = false;
        if (data.code === 0) {
          this.deviceList = data.data.map(device => ({
            device_id: device.id,
            model: device.board,
            firmwareVersion: device.appVersion,
            macAddress: device.macAddress,
            bindTime: device.createDate,
            lastConversation: device.lastConnectedAt,
            remark: device.alias,
            _originalRemark: device.alias,
            isEdit: false,
            _submitting: false,
            otaSwitch: device.autoUpdate === 1,
            rawBindTime: new Date(device.createDate).getTime(),
            selected: false,
            deviceStatus: 'offline'
          })).sort((a, b) => a.rawBindTime - b.rawBindTime);
          this.activeSearchKeyword = "";
          this.searchKeyword = "";
          this.fetchDeviceStatus(agentId);
        } else {
          this.$message.error(data.msg || this.$t('device.getListFailed'));
        }
      });
    },
    fetchDeviceStatus(agentId) {
      this.loading = true;
      Api.device.getDeviceStatus(agentId, ({ data }) => {
        this.loading = false;
        if (data.code === 0) {
          try {
            const statusData = JSON.parse(data.data);
            if (statusData && typeof statusData === 'object') {
              this.mqttServiceAvailable = true;
              this.updateDeviceStatusFromResponse(statusData);
            } else {
              this.mqttServiceAvailable = false;
            }
          } catch (e) {
            this.mqttServiceAvailable = false;
          }
        } else {
          this.mqttServiceAvailable = false;
        }
      });
    },
    updateDeviceStatusFromResponse(deviceStatusMap) {
      this.deviceList.forEach(device => {
        const macAddress = device.macAddress ? device.macAddress.replace(/:/g, '_') : 'unknown';
        const groupId = device.model ? device.model.replace(/:/g, '_') : 'GID_default';
        const mqttClientId = `${groupId}@@@${macAddress}@@@${macAddress}`;

        if (deviceStatusMap[mqttClientId]) {
          const statusInfo = deviceStatusMap[mqttClientId];
          device.deviceStatus = (statusInfo.isAlive === true || (statusInfo.isAlive === null && statusInfo.exists === true)) ? 'online' : 'offline';
        } else {
          device.deviceStatus = 'offline';
        }
      });
    },
    headerCellClassName({ columnIndex }) {
      return columnIndex === 0 ? "custom-selection-header" : "";
    },
    getFirmwareTypeName(type) {
      const firmwareType = this.firmwareTypes.find(item => item.key === type);
      return firmwareType ? firmwareType.name : type;
    },
    updateDeviceInfo(device_id, payload, callback) {
      return Api.device.updateDeviceInfo(device_id, payload, ({ data }) => {
        callback(data.code === 0, data);
      });
    },
    handleOtaSwitchChange(row) {
      this.updateDeviceInfo(row.device_id, { autoUpdate: row.otaSwitch ? 1 : 0 }, (result, { msg }) => {
        if (result) {
          this.$message.success(row.otaSwitch ? this.$t('device.autoUpdateEnabled') : this.$t('device.autoUpdateDisabled'));
        } else {
          row.otaSwitch = !row.otaSwitch;
          this.$message.error(msg || this.$t('message.error'));
        }
      });
    },
    isGenerate(row) {
      const version = row.firmwareVersion.replace(/\./g, '');
      return Number(version) >= 200;
    }
  }
};
</script>
<style lang="scss" scoped>
@import "../styles/aurora-theme.scss";

/* --- Unique styles for DeviceManagement --- */
.device-name-text { color: $accent-cyan; font-weight: bold; }
</style>
