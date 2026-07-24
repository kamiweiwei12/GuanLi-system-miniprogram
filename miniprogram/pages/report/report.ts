import { submitReport, uploadImage } from '../../api/report';

// pages/report/report.ts
Page({
    data: {
        form: {
            department: '',
            employeeName: '',
            phoneNumber: '',
            area: '',
            inspectionTime: '',
            deviceName: '',
            result: 0,
            description: '',
            imageUrls: [] as string[]
        },
        images: [] as string[],
        resultOptions: ['正常', '故障', '隐患'],
        resultIndex: 0,
        uploading: false
    },

    onLoad() {
        // 页面加载
    },

    // 部门
    onDepartmentInput(e: WechatMiniprogram.Input) {
        this.setData({ 'form.department': e.detail.value });
    },

    // 姓名
    onEmployeeNameInput(e: WechatMiniprogram.Input) {
        this.setData({ 'form.employeeName': e.detail.value });
    },

    // 手机号
    onPhoneNumberInput(e: WechatMiniprogram.Input) {
        this.setData({ 'form.phoneNumber': e.detail.value });
    },

    // 区域
    onAreaInput(e: WechatMiniprogram.Input) {
        this.setData({ 'form.area': e.detail.value });
    },

    // 巡检日期
    onDateChange(e: WechatMiniprogram.PickerChange) {
        this.setData({ 'form.inspectionTime': e.detail.value as string });
    },

    // 设备名称
    onDeviceNameInput(e: WechatMiniprogram.Input) {
        this.setData({ 'form.deviceName': e.detail.value });
    },

    // 巡检结果
    onResultChange(e: WechatMiniprogram.PickerChange) {
        const index = Number(e.detail.value);
        this.setData({
            resultIndex: index,
            'form.result': index
        });
    },

    // 详细描述
    onDescriptionInput(e: WechatMiniprogram.TextareaInput) {
        this.setData({ 'form.description': e.detail.value });
    },

    // 选择图片
    chooseImage() {
        if (this.data.uploading) return;
        const remain = 9 - this.data.images.length;
        if (remain <= 0) {
            wx.showToast({ title: '最多上传 9 张图片', icon: 'none' });
            return;
        }

        wx.chooseImage({
            count: remain,
            sizeType: ['compressed'],
            sourceType: ['album', 'camera'],
            success: (res) => {
                this.uploadFiles(res.tempFilePaths);
            }
        });
    },

    // 批量上传
    uploadFiles(filePaths: string[]) {
        this.setData({ uploading: true });
        wx.showLoading({ title: '上传中', mask: true });

        const tasks = filePaths.map((path) =>
            uploadImage(path).catch(() => null)
        );

        Promise.all(tasks).then((urls) => {
            wx.hideLoading();
            const newImages = [...this.data.images];
            const newUrls = [...this.data.form.imageUrls];

            filePaths.forEach((path, i) => {
                if (urls[i]) {
                    newImages.push(path);
                    newUrls.push(urls[i] as string);
                } else {
                    wx.showToast({ title: '部分图片上传失败', icon: 'none' });
                }
            });

            this.setData({
                images: newImages,
                'form.imageUrls': newUrls,
                uploading: false
            });
        });
    },

    // 预览图片
    previewImage(e: WechatMiniprogram.TouchEvent) {
        const index = e.currentTarget.dataset.index as number;
        wx.previewImage({
            current: this.data.images[index],
            urls: this.data.images
        });
    },

    // 删除图片
    deleteImage(e: WechatMiniprogram.TouchEvent) {
        const index = e.currentTarget.dataset.index as number;
        wx.showModal({
            title: '确认删除',
            content: '确定要删除这张图片吗？',
            success: (res) => {
                if (res.confirm) {
                    const images = [...this.data.images];
                    const urls = [...this.data.form.imageUrls];
                    images.splice(index, 1);
                    urls.splice(index, 1);
                    this.setData({
                        images,
                        'form.imageUrls': urls
                    });
                }
            }
        });
    },

    // 提交
    async submit() {
        // 表单校验
        const { form } = this.data;
        if (!form.department.trim()) {
            wx.showToast({ title: '请输入部门', icon: 'none' });
            return;
        }
        if (!form.employeeName.trim()) {
            wx.showToast({ title: '请输入姓名', icon: 'none' });
            return;
        }
        if (!/^1\d{10}$/.test(form.phoneNumber)) {
            wx.showToast({ title: '请输入正确手机号', icon: 'none' });
            return;
        }
        if (!form.area.trim()) {
            wx.showToast({ title: '请输入巡检区域', icon: 'none' });
            return;
        }
        if (!form.inspectionTime) {
            wx.showToast({ title: '请选择巡检日期', icon: 'none' });
            return;
        }
        if (!form.deviceName.trim()) {
            wx.showToast({ title: '请输入设备名称', icon: 'none' });
            return;
        }

        // 提交
        wx.showLoading({ title: '提交中', mask: true });
        try {
            const submitData = {
                department: form.department,
                employeeName: form.employeeName,
                phoneNumber: form.phoneNumber,
                area: form.area,
                inspectionTime: form.inspectionTime + 'T00:00:00',
                deviceName: form.deviceName,
                result: form.result,
                description: form.description,
                imageUrls: form.imageUrls
            };
            await submitReport(submitData);
            wx.showToast({ title: '提交成功', icon: 'success' });
            this.resetForm();
        } catch (err) {
            console.error('提交失败', err);
            wx.showToast({ title: '提交失败', icon: 'none' });
        } finally {
            wx.hideLoading();
        }
    },

    // 重置表单
    resetForm() {
        this.setData({
            form: {
                department: '',
                employeeName: '',
                phoneNumber: '',
                area: '',
                inspectionTime: '',
                deviceName: '',
                result: 0,
                description: '',
                imageUrls: [] as string[]
            },
            images: [] as string[],
            resultIndex: 0,
            uploading: false
        });
    }
});