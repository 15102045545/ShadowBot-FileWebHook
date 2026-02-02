#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
临时脚本：读取并分析 InstructData 文件内容
"""

import pickle
import json
import sys

SOURCE_PATH = r"C:\user-project\xiaokeer-python\shadowbot2\InstructData"

def analyze_clipboard_data():
    """读取并分析剪贴板数据"""
    try:
        with open(SOURCE_PATH, 'rb') as f:
            clipboard_data = pickle.load(f)

        print("=" * 80)
        print("InstructData 文件内容分析")
        print("=" * 80)
        print(f"\n总共包含 {len(clipboard_data)} 种剪贴板格式\n")

        for fmt_id, fmt_data in clipboard_data.items():
            format_id = fmt_data['format_id']
            format_name = fmt_data['format_name']
            data = fmt_data['data']

            print("-" * 80)
            print(f"格式ID: {format_id}")
            print(f"格式名称: {format_name}")

            # 显示数据类型和大小
            data_type = type(data).__name__
            if isinstance(data, bytes):
                data_size = len(data)
            elif isinstance(data, str):
                data_size = len(data.encode('utf-8'))
            else:
                data_size = sys.getsizeof(data)

            print(f"数据类型: {data_type}")
            print(f"数据大小: {data_size} 字节")

            # 尝试显示数据内容
            print(f"数据内容预览:")
            if isinstance(data, str):
                # 文本数据，直接显示
                if len(data) > 1000:
                    print(data[:1000] + "\n... (内容过长，已截断)")
                else:
                    print(data)
            elif isinstance(data, bytes):
                # 二进制数据，尝试解码
                try:
                    text = data.decode('utf-8')
                    if len(text) > 1000:
                        print(text[:1000] + "\n... (内容过长，已截断)")
                    else:
                        print(text)
                except:
                    # 无法解码为文本，显示十六进制
                    hex_preview = data[:100].hex()
                    print(f"(二进制数据) Hex: {hex_preview}...")
            else:
                print(f"(其他类型) {repr(data)[:500]}")

            print()

        print("=" * 80)

        # 返回数据以便进一步处理
        return clipboard_data

    except Exception as e:
        print(f"读取失败: {e}")
        import traceback
        traceback.print_exc()
        return None

if __name__ == "__main__":
    data = analyze_clipboard_data()
