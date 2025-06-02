package com.learning.interrogation.server.flow;

import com.yomahub.liteflow.core.NodeComponent;

import java.util.List;

/**
 * 自定义基础组件类
 * 用于保存已执行主键
 *
 * @author wangwei
 * @version 1.0
 * @date 2025/5/4 下午7:02
 */
public abstract class BaseComponent extends NodeComponent {

    // 执行前记录组件名称
    @Override
    public void beforeProcess() {
        List<BaseComponent> executedComponents = this.getSlot().getRequestData();
        executedComponents.add(this);
    }

    // 出错时回调已执行的组件
    @Override
    public void onError(Exception e) {
        List<BaseComponent> executedComponents = this.getSlot().getRequestData();
        for (int i = executedComponents.size() - 1; i >= 0; i--) {
            BaseComponent component = executedComponents.get(i);
            if (component != this) {
                try {
                    // 调用回调方法
                    component.rollback();
                } catch (Exception rollbackE) {
                    System.out.println("回调组件失败：" + e.getMessage());
                }
            }
        }
    }

    // 回调方法，可在子类中实现具体逻辑
    public abstract void rollback();
}
