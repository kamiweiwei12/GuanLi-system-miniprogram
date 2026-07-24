// pages/detail/detail.ts
import { getReportDetail } from '../../api/report';

interface InspectionDetail {
    id: number;
    department: string;
    employeeName: string;
    phoneNumber: string;
    area: string;
    inspectionTime: string;
    deviceName: string;
    result: number;
    description: string;
    imageUrls: string[];
}

function getResultClass(result: number): string {
    if (result === 0) return 'success';
    if (result === 1) return 'danger';
    return 'warning';
}

function getResultText(result: number): string {
    if (result === 0) return '正常';
    if (result === 1) return '故障';
    return '隐患';
}

Page({
    data: {
        detail: null as InspectionDetail | null,
        resultClass: '',
        resultText: '',
        loading: true,
        error: false
    },

    onLoad(options: { id?: string }) {
        const id = Number(options.id);
        if (!id) {
            wx.showToast({ title: '记录ID无效', icon: 'none' });
            setTimeout(() => wx.navigateBack(), 1500);
            return;
        }

        wx.showLoading({ title: '加载中' });
        getReportDetail(id)
            .then((data) => {
                const detail = data as InspectionDetail;
                this.setData({
                    detail,
                    resultClass: getResultClass(detail.result),
                    resultText: getResultText(detail.result),
                    loading: false
                });
            })
            .catch(() => {
                this.setData({ error: true, loading: false });
                wx.showToast({ title: '加载失败', icon: 'none' });
            })
            .finally(() => {
                wx.hideLoading();
            });
    },

    previewImage(e: WechatMiniprogram.TouchEvent) {
        const index = e.currentTarget.dataset.index as number;
        const urls = this.data.detail?.imageUrls || [];
        if (urls.length === 0) return;
        wx.previewImage({
            current: urls[index],
            urls
        });
    }
});
