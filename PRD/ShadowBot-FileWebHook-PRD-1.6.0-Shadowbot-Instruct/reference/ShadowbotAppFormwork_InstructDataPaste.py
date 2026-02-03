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
SOURCE_PATH = r"C:\Users\Administrator\PycharmProjects\xiaokee-python\InstructData\InstructData\InstructData"

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

                    # 处理自定义格式ID（>= 0xC000 即 49152）
                    # 自定义格式ID是机器相关的，需要用格式名重新注册获取当前机器的正确ID
                    actual_format_id = format_id
                    if format_id >= 0xC000 and format_name and format_name.startswith('Format_') is False:
                        try:
                            # 用格式名在当前机器上注册/获取正确的格式ID
                            actual_format_id = win32clipboard.RegisterClipboardFormat(format_name)
                            if actual_format_id != format_id:
                                print(f"  ℹ️  格式 {format_name}: ID {format_id} -> {actual_format_id} (已映射)")
                        except Exception as reg_err:
                            print(f"  ⚠️  注册格式 {format_name} 失败: {reg_err}, 使用原ID")

                    # 设置剪贴板数据
                    win32clipboard.SetClipboardData(actual_format_id, data)

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
