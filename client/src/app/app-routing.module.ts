import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CharacterComponent } from './components/character.component';
import { CommentComponent } from './components/comment.component';
import { EditcommentsComponent } from './components/editcomments.component';
import { SearchComponent } from './components/search.component';

const routes: Routes = [
  { path: '', component: SearchComponent },
  { path: 'character/:id', component: CharacterComponent }, // set the path for the parameterized route to retrive the id
  { path: 'character/:id/comment', component: CommentComponent},
  { path: 'character/:id/editcomment/:commentId', component: EditcommentsComponent},
  { path: '**', redirectTo: '/', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
