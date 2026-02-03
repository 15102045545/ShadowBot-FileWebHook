#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
影刀流程指令粘贴脚本
功能: 从文件中读取保存的剪贴板数据，并恢复到系统剪贴板
使用方法: 运行此脚本后，在影刀中按Ctrl+V粘贴即可
"""

import win32clipboard
import pickle
import os
import sys

# 源数据路径
SOURCE_PATH = r"/PRD/shadowbot/InstructData"


def restore_clipboard_data():
    """
    从文件中读取数据并恢复到剪贴板
    """
    try:
        # 检查文件是否存在
        if not os.path.exists(SOURCE_PATH):
            print(f"❌ 文件不存在: {SOURCE_PATH}")
            print("   请先使用复制脚本保存数据")
            return False

        # 读取序列化的数据
        with open(SOURCE_PATH, 'rb') as f:
            clipboard_data = pickle.load(f)

        if not clipboard_data:
            print("⚠️  文件中没有可恢复的数据")
            return False

        print(f"📋 检测到 {len(clipboard_data)} 种剪贴板格式")

        # 打开剪贴板并清空
        win32clipboard.OpenClipboard()
        try:
            win32clipboard.EmptyClipboard()

            # 恢复所有格式的数据
            success_count = 0
            for fmt_id, fmt_data in clipboard_data.items():
                try:
                    format_id = fmt_data['format_id']
                    format_name = fmt_data['format_name']
                    data = fmt_data['data']

                    # 设置剪贴板数据
                    win32clipboard.SetClipboardData(format_id, data)

                    # 显示数据大小
                    if isinstance(data, bytes):
                        data_size = len(data)
                    elif isinstance(data, str):
                        data_size = len(data.encode('utf-8'))
                    else:
                        data_size = sys.getsizeof(data)

                    print(f"  ✓ 格式 {format_id} ({format_name}): {data_size} 字节")
                    success_count += 1

                except Exception as e:
                    print(f"  ⚠️  格式 {format_id} 恢复失败: {e}")
                    continue
        finally:
            win32clipboard.CloseClipboard()

        print(f"\n✅ 成功恢复 {success_count}/{len(clipboard_data)} 种格式到剪贴板")
        return True

    except Exception as e:
        print(f"❌ 恢复失败: {e}")
        import traceback
        traceback.print_exc()
        return False


if __name__ == "__main__":
    print("=" * 60)
    print("🟢 影刀流程指令粘贴工具")
    print("=" * 60)
    print()

    success = restore_clipboard_data()

    print()
    if success:
        print("✨ 剪贴板已恢复！现在可以在影刀中按 Ctrl+V 粘贴了")
        print("💡 提示: 请尽快粘贴，避免剪贴板被其他操作覆盖")
    else:
        print("💥 操作失败，请检查错误信息")

    print()
    input("按回车键退出...")
