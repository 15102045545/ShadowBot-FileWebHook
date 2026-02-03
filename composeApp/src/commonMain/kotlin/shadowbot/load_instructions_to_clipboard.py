#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
影刀指令加载到剪贴板脚本

功能: 从文件中读取保存的剪贴板数据，并恢复到系统剪贴板
用于将修改后的 FileWebHook-App-Framework 框架指令写入剪贴板，供用户粘贴到影刀

用法:
    python load_instructions_to_clipboard.py --input <输入文件路径>
"""

import win32clipboard
import pickle
import os
import sys
import json
import argparse


def load_instructions_to_clipboard(input_path):
    """
    从文件中读取数据并恢复到剪贴板

    Args:
        input_path: 输入文件路径

    Returns:
        dict: 操作结果，包含 success, message, successCount, totalCount
    """
    result = {
        "success": False,
        "message": "",
        "successCount": 0,
        "totalCount": 0
    }

    try:
        # 检查文件是否存在
        if not os.path.exists(input_path):
            result["message"] = f"文件不存在: {input_path}"
            return result

        # 读取序列化的数据
        with open(input_path, 'rb') as f:
            clipboard_data = pickle.load(f)

        if not clipboard_data:
            result["message"] = "文件中没有可恢复的数据"
            return result

        result["totalCount"] = len(clipboard_data)

        # 打开剪贴板并清空
        win32clipboard.OpenClipboard()
        try:
            win32clipboard.EmptyClipboard()

            # 恢复所有格式的数据
            success_count = 0
            for fmt_id, fmt_data in clipboard_data.items():
                try:
                    format_id = fmt_data['format_id']
                    format_name = fmt_data.get('format_name', '')
                    data = fmt_data['data']

                    # 处理自定义格式ID（>= 0xC000 即 49152）
                    # 自定义格式ID是机器相关的，需要用格式名重新注册获取当前机器的正确ID
                    actual_format_id = format_id
                    if format_id >= 0xC000 and format_name and not format_name.startswith('Format_'):
                        try:
                            # 用格式名在当前机器上注册/获取正确的格式ID
                            actual_format_id = win32clipboard.RegisterClipboardFormat(format_name)
                        except Exception:
                            # 注册失败，使用原ID
                            pass

                    # 设置剪贴板数据
                    win32clipboard.SetClipboardData(actual_format_id, data)
                    success_count += 1

                except Exception as e:
                    # 单个格式恢复失败，继续处理其他格式
                    continue

        finally:
            win32clipboard.CloseClipboard()

        result["successCount"] = success_count

        if success_count > 0:
            result["success"] = True
            result["message"] = f"成功恢复 {success_count}/{result['totalCount']} 种格式到剪贴板"
        else:
            result["message"] = "未能恢复任何数据到剪贴板"

        return result

    except Exception as e:
        result["message"] = f"恢复失败: {str(e)}"
        return result


def main():
    """主函数"""
    parser = argparse.ArgumentParser(
        description='将文件中的数据恢复到系统剪贴板'
    )
    parser.add_argument(
        '--input', '-i',
        required=True,
        help='输入文件路径 (ShadowbotAppFormwork_InstructData)'
    )

    args = parser.parse_args()

    result = load_instructions_to_clipboard(args.input)

    # 输出JSON格式的结果
    print(json.dumps(result, ensure_ascii=False, indent=2))

    return 0 if result["success"] else 1


if __name__ == "__main__":
    sys.exit(main())
