import {Component} from '@angular/core';
import {MatCard, MatCardContent, MatCardImage} from '@angular/material/card';
import {Markdown} from '../shared/markdown/markdown';

@Component({
  selector: 'app-home',
  templateUrl: './home.html',
  styleUrls: ['./home.scss'],
  imports: [
    MatCard,
    MatCardImage,
    MatCardContent,
    Markdown,
  ],
  preserveWhitespaces: true
})
export class Home {

}
