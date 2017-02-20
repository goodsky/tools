namespace ImageSorter
{
    partial class SelectImages
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.ImageSetsList = new System.Windows.Forms.ListView();
            this.ImageSetsLabel = new System.Windows.Forms.Label();
            this.ImageSetsAddButton = new System.Windows.Forms.Button();
            this.DoneButton = new System.Windows.Forms.Button();
            this.ImageSetsRemoveButton = new System.Windows.Forms.Button();
            this.ImageSetsInstructions = new System.Windows.Forms.Label();
            this.RestoreButton = new System.Windows.Forms.Button();
            this.SuspendLayout();
            // 
            // ImageSetsList
            // 
            this.ImageSetsList.Alignment = System.Windows.Forms.ListViewAlignment.Default;
            this.ImageSetsList.Location = new System.Drawing.Point(11, 25);
            this.ImageSetsList.MultiSelect = false;
            this.ImageSetsList.Name = "ImageSetsList";
            this.ImageSetsList.Size = new System.Drawing.Size(381, 104);
            this.ImageSetsList.TabIndex = 0;
            this.ImageSetsList.UseCompatibleStateImageBehavior = false;
            this.ImageSetsList.View = System.Windows.Forms.View.List;
            // 
            // ImageSetsLabel
            // 
            this.ImageSetsLabel.AutoSize = true;
            this.ImageSetsLabel.Location = new System.Drawing.Point(12, 9);
            this.ImageSetsLabel.Name = "ImageSetsLabel";
            this.ImageSetsLabel.Size = new System.Drawing.Size(44, 13);
            this.ImageSetsLabel.TabIndex = 3;
            this.ImageSetsLabel.Text = "Images:";
            // 
            // ImageSetsAddButton
            // 
            this.ImageSetsAddButton.Location = new System.Drawing.Point(398, 25);
            this.ImageSetsAddButton.Name = "ImageSetsAddButton";
            this.ImageSetsAddButton.Size = new System.Drawing.Size(75, 49);
            this.ImageSetsAddButton.TabIndex = 5;
            this.ImageSetsAddButton.Text = "+";
            this.ImageSetsAddButton.UseVisualStyleBackColor = true;
            this.ImageSetsAddButton.Click += new System.EventHandler(this.ImageSetsAddButton_Click);
            // 
            // DoneButton
            // 
            this.DoneButton.Location = new System.Drawing.Point(10, 135);
            this.DoneButton.Name = "DoneButton";
            this.DoneButton.Size = new System.Drawing.Size(75, 23);
            this.DoneButton.TabIndex = 6;
            this.DoneButton.Text = "Done";
            this.DoneButton.UseVisualStyleBackColor = true;
            this.DoneButton.Click += new System.EventHandler(this.DoneButton_Click);
            // 
            // ImageSetsRemoveButton
            // 
            this.ImageSetsRemoveButton.Location = new System.Drawing.Point(398, 80);
            this.ImageSetsRemoveButton.Name = "ImageSetsRemoveButton";
            this.ImageSetsRemoveButton.Size = new System.Drawing.Size(75, 49);
            this.ImageSetsRemoveButton.TabIndex = 7;
            this.ImageSetsRemoveButton.Text = "-";
            this.ImageSetsRemoveButton.UseVisualStyleBackColor = true;
            this.ImageSetsRemoveButton.Click += new System.EventHandler(this.ImageSetsRemoveButton_Click);
            // 
            // ImageSetsInstructions
            // 
            this.ImageSetsInstructions.AutoSize = true;
            this.ImageSetsInstructions.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Italic, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.ImageSetsInstructions.Location = new System.Drawing.Point(164, 9);
            this.ImageSetsInstructions.Name = "ImageSetsInstructions";
            this.ImageSetsInstructions.Size = new System.Drawing.Size(228, 13);
            this.ImageSetsInstructions.TabIndex = 8;
            this.ImageSetsInstructions.Text = "Click \'+\' button to add a sorted range of images";
            // 
            // RestoreButton
            // 
            this.RestoreButton.Location = new System.Drawing.Point(398, 135);
            this.RestoreButton.Name = "RestoreButton";
            this.RestoreButton.Size = new System.Drawing.Size(75, 23);
            this.RestoreButton.TabIndex = 9;
            this.RestoreButton.Text = "Restore";
            this.RestoreButton.UseVisualStyleBackColor = true;
            this.RestoreButton.Click += new System.EventHandler(this.RestoreButton_Click);
            // 
            // SelectImages
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(481, 166);
            this.Controls.Add(this.RestoreButton);
            this.Controls.Add(this.ImageSetsInstructions);
            this.Controls.Add(this.ImageSetsRemoveButton);
            this.Controls.Add(this.DoneButton);
            this.Controls.Add(this.ImageSetsAddButton);
            this.Controls.Add(this.ImageSetsLabel);
            this.Controls.Add(this.ImageSetsList);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedToolWindow;
            this.Name = "SelectImages";
            this.Text = "Select Images";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.ListView ImageSetsList;
        private System.Windows.Forms.Label ImageSetsLabel;
        private System.Windows.Forms.Button ImageSetsAddButton;
        private System.Windows.Forms.Button DoneButton;
        private System.Windows.Forms.Button ImageSetsRemoveButton;
        private System.Windows.Forms.Label ImageSetsInstructions;
        private System.Windows.Forms.Button RestoreButton;
    }
}

