#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
影刀流程指令复制脚本
功能: 将系统剪贴板中的内容（包括所有格式）保存到文件中
使用方法: 在影刀中复制指令后，直接运行此脚本
"""

import win32clipboard
import pickle
import os
import sys

# 目标保存路径
SAVE_PATH = r"/PRD/shadowbot/InstructData"


def get_all_clipboard_formats():
    """
    获取剪贴板中所有可用的数据格式
    返回格式ID列表
    """
    formats = []
    win32clipboard.OpenClipboard()
    try:
        current_format = 0
        while True:
            current_format = win32clipboard.EnumClipboardFormats(current_format)
            if current_format == 0:
                break
            formats.append(current_format)
    finally:
        win32clipboard.CloseClipboard()
    return formats


def get_clipboard_format_name(format_id):
    """
    获取剪贴板格式名称
    """
    try:
        name = win32clipboard.GetClipboardFormatName(format_id)
        return name
    except:
        # 标准格式没有名称，返回格式ID
        return f"Format_{format_id}"


def save_clipboard_data():
    """
    保存剪贴板中的所有格式数据到文件
    """
    try:
        # 确保目标目录存在
        os.makedirs(os.path.dirname(SAVE_PATH), exist_ok=True)

        # 获取所有剪贴板格式
        formats = get_all_clipboard_formats()

        if not formats:
            print("⚠️  剪贴板为空，没有可保存的内容")
            return False

        print(f"📋 检测到 {len(formats)} 种剪贴板格式")

        # 存储所有格式的数据
        clipboard_data = {}

        win32clipboard.OpenClipboard()
        try:
            for fmt in formats:
                try:
                    # 获取数据
                    data = win32clipboard.GetClipboardData(fmt)

                    # 获取格式名称
                    fmt_name = get_clipboard_format_name(fmt)

                    # 保存数据
                    clipboard_data[fmt] = {
                        'format_id': fmt,
                        'format_name': fmt_name,
                        'data': data
                    }

                    # 显示数据大小
                    if isinstance(data, bytes):
                        data_size = len(data)
                    elif isinstance(data, str):
                        data_size = len(data.encode('utf-8'))
                    else:
                        data_size = sys.getsizeof(data)

                    print(f"  ✓ 格式 {fmt} ({fmt_name}): {data_size} 字节")

                except Exception as e:
                    print(f"  ⚠️  格式 {fmt} 读取失败: {e}")
                    continue
        finally:
            win32clipboard.CloseClipboard()

        # 使用pickle序列化保存
        with open(SAVE_PATH, 'wb') as f:
            pickle.dump(clipboard_data, f, protocol=pickle.HIGHEST_PROTOCOL)

        print(f"\n✅ 成功保存到: {SAVE_PATH}")
        print(f"📦 总计保存 {len(clipboard_data)} 种格式")
        return True

    except Exception as e:
        print(f"❌ 保存失败: {e}")
        import traceback
        traceback.print_exc()
        return False


if __name__ == "__main__":
    print("=" * 60)
    print("🔵 影刀流程指令复制工具")
    print("=" * 60)
    print()

    success = save_clipboard_data()

    print()
    if success:
        print("✨ 操作完成！现在可以在其他电脑上使用粘贴脚本了")
    else:
        print("💥 操作失败，请检查错误信息")

    print()
    input("按回车键退出...")
