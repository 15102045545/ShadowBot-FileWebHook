#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
影刀指令剪贴板转储脚本

功能: 将系统剪贴板中的所有格式数据（包括影刀自定义格式）保存到文件
用于将 FileWebHook-App-Framework 框架指令转储为元指令文件

用法:
    python dump_clipboard_to_file.py --output <输出文件路径>
"""

import win32clipboard
import pickle
import os
import sys
import json
import argparse


def get_all_clipboard_formats():
    """
    获取剪贴板中所有可用的数据格式

    Returns:
        list: 格式ID列表
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

    Args:
        format_id: 格式ID

    Returns:
        str: 格式名称，标准格式返回 "Format_{id}"
    """
    try:
        name = win32clipboard.GetClipboardFormatName(format_id)
        return name
    except:
        return f"Format_{format_id}"


def dump_clipboard_to_file(output_path):
    """
    将剪贴板中的所有格式数据保存到文件

    Args:
        output_path: 输出文件路径

    Returns:
        dict: 操作结果，包含 success, message, formatCount, outputPath
    """
    result = {
        "success": False,
        "message": "",
        "formatCount": 0,
        "outputPath": output_path
    }

    try:
        # 确保目标目录存在
        os.makedirs(os.path.dirname(output_path), exist_ok=True)

        # 获取所有剪贴板格式
        formats = get_all_clipboard_formats()

        if not formats:
            result["message"] = "剪贴板为空，没有可保存的内容"
            return result

        # 存储所有格式的数据
        clipboard_data = {}

        win32clipboard.OpenClipboard()
        try:
            success_count = 0
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

                    success_count += 1

                except Exception as e:
                    # 某些格式可能无法读取，跳过
                    continue
        finally:
            win32clipboard.CloseClipboard()

        if not clipboard_data:
            result["message"] = "未能读取任何剪贴板数据"
            return result

        # 使用pickle序列化保存
        with open(output_path, 'wb') as f:
            pickle.dump(clipboard_data, f, protocol=pickle.HIGHEST_PROTOCOL)

        result["success"] = True
        result["message"] = f"成功保存 {len(clipboard_data)} 种格式到文件"
        result["formatCount"] = len(clipboard_data)

        return result

    except Exception as e:
        result["message"] = f"保存失败: {str(e)}"
        return result


def main():
    """主函数"""
    parser = argparse.ArgumentParser(
        description='将系统剪贴板内容转储到文件'
    )
    parser.add_argument(
        '--output', '-o',
        required=True,
        help='输出文件路径'
    )

    args = parser.parse_args()

    result = dump_clipboard_to_file(args.output)

    # 输出JSON格式的结果
    print(json.dumps(result, ensure_ascii=False, indent=2))

    return 0 if result["success"] else 1


if __name__ == "__main__":
    sys.exit(main())
