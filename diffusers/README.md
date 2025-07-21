# Diffusion Model Toolbox

Personal repository to play around with diffusion models. Nothing novel or fancy.

## Setup
1. Using Python 3.11
1. `pip install -r requirements.txt --index-url https://download.pytorch.org/whl/cu126`

## Packages
* PyTorch (`pip install torch --index-url https://download.pytorch.org/whl/cu126`)
* diffusers (for stable diffusion - image generation)
* transformers (for text understanding)
* accelerate (automatically utilize GPUs intelligently)
* PEFT (parameter-efficient fine-tuning for LoRA weights)
* ipywidgets (to show progress while downloading huggingface models)
* hf_xet (XET for downloading large files from Git)
* scipi (for pixel detection math)