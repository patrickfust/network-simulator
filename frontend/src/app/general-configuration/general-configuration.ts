import {Component, inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {GeneralConfigurationService} from '../services/general-configuration.service';
import {GeneralConfiguration} from '../models/general-configuration';
import {materialImports} from '../shared/material-imports';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatCard, MatCardContent, MatCardHeader, MatCardImage, MatCardTitle} from '@angular/material/card';

@Component({
  selector: 'app-general-configuration',
  templateUrl: './general-configuration.html',
  styleUrls: ['./general-configuration.scss'],
  imports: [
    materialImports,
    MatCard,
    MatCardImage,
    MatCardContent,
    MatCardTitle,
    MatCardHeader,
  ]
})
export class GeneralConfigurationComponent implements OnInit {
  readonly snackBar = inject(MatSnackBar);
  generalConfigForm: FormGroup;
  generalConfigurationService = inject(GeneralConfigurationService);

  constructor(private fb: FormBuilder) {
    this.generalConfigForm = this.fb.group({
      targetBaseUrl: ['', [Validators.required, Validators.pattern('https?://.+')]],
      timeoutMs: [0, [Validators.required, Validators.min(1)]],
      followRedirect: [false],
    });
  }

  ngOnInit(): void {
    this.generalConfigurationService.getGeneralConfiguration().subscribe({
      next: (config: GeneralConfiguration) => {
        this.generalConfigForm.patchValue(config);
      },
      error: (err) => {
        console.error('Failed to load configuration', err);
        this.snackBar.open('Failed to load configuration', 'Close', { duration: 3000 });
      }
    });
  }

  onSubmit(): void {
    if (this.generalConfigForm.valid) {
      const updatedConfig: GeneralConfiguration = this.generalConfigForm.value;
      console.log(updatedConfig);
      this.generalConfigurationService.updateGeneralConfiguration(updatedConfig).subscribe({
        next: (res) => {
          this.snackBar.open('Configuration updated successfully', 'Close', { duration: 3000 });
        },
        error: (err) => {
          console.error('Failed to update configuration', err)
          this.snackBar.open('Failed to update configuration', 'Close', { duration: 3000 });
        },
      });
    }
  }
}
