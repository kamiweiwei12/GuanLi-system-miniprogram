import { queryReport } from '../../api/report';

interface InspectionItem {
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
    createdAt: string;
}

interface PageData {
    list: InspectionItem[];
    pageNum: number;
    pageSize: number;
    hasMore: boolean;
    loading: boolean;
    total: number;
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
        list: [],
        pageNum: 1,
        pageSize: 10,
        hasMore: true,
        loading: false,
        total: 0
    },

    onLoad() {
        this.loadData(true);
    },

    onPullDownRefresh() {
        this.loadData(true);
    },

    onReachBottom() {
        if (!this.data.hasMore || this.data.loading) return;
        this.loadData(false);
    },

    async loadData(reset: boolean) {
        if (this.data.loading) return;

        this.setData({ loading: true });
        const pageNum = reset ? 1 : this.data.pageNum + 1;

        try {
            const res = await queryReport({
                pageNum,
                pageSize: this.data.pageSize
            });

            const newList = reset
                ? res.list || []
                : [...this.data.list, ...(res.list || [])];

            this.setData({
                list: newList.map((item: InspectionItem) => ({
                    ...item,
                    resultClass: getResultClass(item.result),
                    resultText: getResultText(item.result)
                })),
                pageNum,
                hasMore: newList.length < res.total,
                total: res.total
            });
        } catch (_err) {
            wx.showToast({ title: '加载失败', icon: 'none' });
        } finally {
            this.setData({ loading: false });
            wx.stopPullDownRefresh();
        }
    },

    goToDetail(e: WechatMiniprogram.TouchEvent) {
        const id = e.currentTarget.dataset.id as number;
        wx.navigateTo({ url: `/pages/detail/detail?id=${id}` });
    }
});